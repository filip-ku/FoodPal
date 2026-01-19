package client.scenes;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Ingredient;
import commons.Recipe;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;

public class IngredientsOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Map<Long, Integer> ingredientUsageCount = new HashMap<>();

    private ObservableList<Ingredient> data;

    @FXML
    private TableView<Ingredient> tableIngredients;
    @FXML
    private TableColumn<Ingredient, String> colName;
    @FXML
    private TableColumn<Ingredient, Double> colProtein;
    @FXML
    private TableColumn<Ingredient, Double> colFat;
    @FXML
    private TableColumn<Ingredient, Double> colCarbs;
    @FXML
    private TableColumn<Ingredient, Double> colCalories;
    @FXML
    private TableColumn<Ingredient, Integer> colNumOfRecipes;
    @FXML
    private Button seeRecipesButton;
    @FXML
    private Button editIngredientButton;

    @FXML
    private ResourceBundle resources;

    /**
     * Constructs a {@code IngredientsOverviewCtrl}.
     *
     * <p>Instances are created via Guice injection. The controller receives a
     * reference to {@link ServerUtils} for server communication and a
     * {@link MainCtrl} for navigation.</p>
     *
     * @param server  injected {@link ServerUtils}
     * @param mainCtrl injected {@link MainCtrl}
     */
    @Inject
    public IngredientsOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Called by the JavaFX framework after the FXML elements have been injected.
     *
     * <p>Initialises UI bindings and listeners, then shows the default
     * "main menu" view.</p>
     *
     * @param location  location of the FXML file (unused)
     * @param resources resource bundle for internationalisation
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        colName.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));

        colProtein.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getProteinPer100g()).asObject());

        colFat.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getFatPer100g()).asObject());

        colCarbs.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getCarbsPer100g()).asObject());

        colCalories.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getCalories()).asObject());

        colNumOfRecipes.setCellValueFactory(cell ->
                new SimpleIntegerProperty(
                        ingredientUsageCount.getOrDefault(
                                cell.getValue().getId(), 0
                        )
                ).asObject()
        );

        editIngredientButton.setDisable(true);
        tableIngredients.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    editIngredientButton.setDisable(newSel == null);
                });

        seeRecipesButton.setDisable(true);
        tableIngredients.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    editIngredientButton.setDisable(newSel == null);
                    seeRecipesButton.setDisable(newSel == null);
                });
    }

    /**
     * Refreshes the recipe list from the server and updates the table view,
     * recalculates how many recipes each ingredient is used in, and updates
     * the table view accordingly.
     */
    public void refresh() {
        try {
            var ingredients = server.getIngredients();

            updateIngredientUsageCounts(ingredients);

            data = FXCollections.observableList(ingredients);
            tableIngredients.setItems(data);
            tableIngredients.refresh();

        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Navigates to the “Add Ingredient” screen.
     */
    public void addGlobalIngredient() {
        mainCtrl.showAddIngredient();
    }

    /**
     * Checks to see if there is an ingredient selected and navigates to the
     * addIngredient screen in edit mode if so.
     */
    public void editGlobalIngredient() {
        Ingredient selected = tableIngredients.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mainCtrl.showError(resources.getString("ingredientOverview.error.noSelection"));
            return;
        }

        mainCtrl.showEditIngredient(selected);
    }

    /**
     * Navigates to the “Recipe Overview” screen.
     */
    public void goBackToRecipeOverview() {
        mainCtrl.showRecipeOverview();
    }

    /**
     * Deletes the currently selected ingredient after user confirmation.
     *
     * <p>Shows a confirmation dialog. If confirmed, removes the ingredient
     * from the observable list and updates the table view.</p>
     */
    public void deleteGlobalIngredient() {
        Ingredient selected = tableIngredients.getSelectionModel().getSelectedItem();
        refresh();
        int usageCount = ingredientUsageCount.getOrDefault(selected.getId(), 0);

        if (selected == null) {
            mainCtrl.showError(resources.getString("ingredientOverview.error.noSelection"));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(resources.getString("ingredientOverview.dialog.confirmDelete"));
        confirm.setHeaderText(resources.getString("ingredientOverview.dialog.deleteIngredient"));

        if (usageCount > 0) {
            String recipeWord = (usageCount == 1) 
                    ? resources.getString("ingredientOverview.dialog.recipe") 
                    : resources.getString("ingredientOverview.dialog.recipes");
            confirm.setContentText(
                    resources.getString("ingredientOverview.dialog.deleteUsedIngredient")
                            .replace("{0}", String.valueOf(usageCount))
                            .replace("{1}", recipeWord));
        } else {
            confirm.setContentText(resources.getString("ingredientOverview.dialog.deleteConfirm"));
        }

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            server.deleteIngredient(selected);
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return;
        }

        data.remove(selected);
    }

    /**
     * Recomputes how many recipes each ingredient is used in.
     * @param ingredients list containing the ingredients, which need their recipes counted.
     */
    private void updateIngredientUsageCounts(Iterable<Ingredient> ingredients) {
        var recipes = server.getRecipes();

        ingredientUsageCount.clear();

        for (Ingredient ingredient : ingredients) {
            int count = 0;

            for (Recipe recipe : recipes) {
                if (recipe.getIngredients() != null &&
                        recipe.getIngredients().stream()
                                .anyMatch(ri ->
                                        ri.getIngredient().getId()
                                                .equals(ingredient.getId())
                                )) {
                    count++;
                }
            }

            ingredientUsageCount.put(ingredient.getId(), count);
        }
    }

    /**
     * AI generated javadoc.
     * Shows the Recipe Overview screen with a search pre-filled for the selected ingredient.
     * The search is automatically executed to filter recipes containing this ingredient.
     */
    @FXML
    public void showRecipesUsingIngredient() {
        Ingredient selected = tableIngredients.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mainCtrl.showError(resources.getString("ingredientOverview.error.noSelection"));
            return;
        }

        String ingredientName = selected.getName();
        mainCtrl.showRecipeOverviewWithSearch(ingredientName);
    }
}
