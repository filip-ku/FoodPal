package client.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.nio.file.Files;
import java.util.ResourceBundle;

import client.utils.RecipeFormatter;
import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

public class RecipeOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<Recipe> data;

    @FXML
    private TableView<Recipe> tableRecipes;
    @FXML
    private TableColumn<Recipe, String> colRecipes;

    @FXML
    private TableView<RecipeIngredient> tableIngredients;
    @FXML
    private TableColumn<RecipeIngredient, String> colIngredients;
    @FXML
    private TableColumn<RecipeIngredient, String> colAmount;

    @FXML
    private TableView<RecipeStep> tablePreparation;
    @FXML
    private TableColumn<RecipeStep, String> colPreparation;

    @FXML
    private Label recipeName;
    @FXML
    private TextField recipeEditBox;
    @FXML
    private Button recipeEditButton;
    private boolean editingName = false;

    @FXML
    private Button addRecipeStep;
    @FXML
    private Button removeStepButton;
    @FXML
    private Button editStepsButton;

    @FXML
    private Button recipeIngredientAdd;
    @FXML
    private Button recipeIngredientDelete;

    @FXML
    private Button downloadRecipeButton;

    /**
     * Constructs a {@code RecipeOverviewCtrl}.
     *
     * <p>Instances are created via Guice injection. The controller receives a
     * reference to {@link ServerUtils} for server communication and a
     * {@link MainCtrl} for navigation.</p>
     *
     * @param server  injected {@link ServerUtils}
     * @param mainCtrl injected {@link MainCtrl}
     */
    @Inject
    public RecipeOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Called by the JavaFX framework after the FXML elements have been injected.
     *
     * <p>Initialises UI bindings and listeners, then shows the default
     * “main menu” view.</p>
     *
     * @param location  location of the FXML file (unused)
     * @param resources resource bundle for internationalisation (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showMainMenu();

        colRecipes.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitle()));

        colIngredients.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getIngredient().getName())
        );

        colAmount.setCellValueFactory(cell -> {
            var ri = cell.getValue();

            boolean hasFormal = ri.getAmount() != null &&
                            ri.getAmount() != 0 &&
                            ri.getUnit() != null;

            String value = hasFormal
                    ? ri.getAmount() + " " + ri.getUnit()
                    : ri.getInformalAmount(); // ternary operator that executes the code
                                              // to the left of the : if the condition is true and
                                              // the code on the right otherwise

            return new SimpleStringProperty(value);
        });

        colPreparation.setCellValueFactory(cell ->
                new SimpleStringProperty(formatStepForDisplay(cell.getValue())));

        tableRecipes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        recipeName.setText(newSel.getTitle());

                        if (newSel.getIngredients() != null) {
                            tableIngredients.setItems(
                                    FXCollections.observableArrayList(newSel.getIngredients()));
                        } else {
                            tableIngredients.getItems().clear();
                        }

                        loadRecipeOverviewUI();

                        loadStepsForRecipe(newSel);
                    }
                });
    }

    /**
     * Navigates to the “Add Recipe” screen.
     */
    public void addRecipe() {
        mainCtrl.showAddRecipe();
    }

    /**
     * Selects the given recipe in the table.
     *
     * @param recipe the recipe to select
     */
    public void selectRecipe(Recipe recipe) {
        if (recipe == null) return;
        tableRecipes.getSelectionModel().select(recipe);
        loadStepsForRecipe(recipe);
    }

    /**
     * Refreshes the recipe list from the server and updates the table view.
     */
    public void refresh() {
        try {
            var recipes = server.getRecipes();
            data = FXCollections.observableList(recipes);
            tableRecipes.setItems(data);

            Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadStepsForRecipe(selected);
            }
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }
    }

    /**
     * Loads and displays all recipe steps for the specified recipe.
     *
     * @param recipe the recipe whose steps should be shown
     */
    public void loadStepsForRecipe(Recipe recipe) {
        try {
            var steps = server.getStepsForRecipe(recipe.getId());
            ObservableList<RecipeStep> stepData = FXCollections.observableList(steps);
            tablePreparation.setItems(stepData);
            tablePreparation.refresh();
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }
    }

    /**
     * Handles the Edit/Save button click for a recipe name.
     *
     * <p>When not in edit mode, switches to an editable {@link TextField}.
     * When already editing, validates the input and updates the model.</p>
     */
    public void editNameClicked() {
        if (!editingName) {
            editingName = true;
            recipeEditButton.setText("Save");

            recipeEditBox.setText(recipeName.getText());
            recipeEditBox.setDisable(false);
            recipeEditBox.setVisible(true);

            recipeName.setVisible(false);
        } else {
            String newName = recipeEditBox.getText();

            if (newName == null || newName.trim().isEmpty()) {
                mainCtrl.showError("Name cannot be empty.");
                return;
            }
            recipeName.setText(newName);

            editingName = false;
            recipeEditButton.setText("Edit");

            Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setTitle(newName);
            }

            try {
                server.updateRecipe(selected);
            } catch (WebApplicationException e) {
                mainCtrl.showExceptionErrorPopUp(e);
                return;
            }

            tableRecipes.refresh();

            recipeEditBox.setDisable(true);
            recipeEditBox.setVisible(false);

            recipeName.setVisible(true);
        }
    }

    /**
     * Deletes the currently selected recipe after user confirmation.
     *
     * <p>Shows a confirmation dialog. If confirmed, removes the recipe
     * from the observable list and updates the table view.</p>
     */
    public void deleteRecipe() {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mainCtrl.showError("No recipe selected.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Recipe?");
        confirm.setContentText("You sure you wanna delete this recipe?");

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            server.deleteRecipe(selected);
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return;
        }

        showMainMenu();

        data.remove(selected);
    }

    /**
     * Method is not available yet
     */
    @FXML
    private void editSteps() {
        // TODO: implement later
        mainCtrl.showError("Editing steps is not implemented yet.");
    }

    /**
     * Deletes the currently selected ingredient after user confirmation.
     *
     * <p>Shows a confirmation dialog. If confirmed, removes the ingredient
     * from the observable list and updates the table view.</p>
     */
    @FXML
    public void deleteIngredient() {
        Recipe selectedRecipe = tableRecipes.getSelectionModel().getSelectedItem();
        RecipeIngredient selectedRecipeIngredient =
                tableIngredients.getSelectionModel().getSelectedItem();
        if (selectedRecipeIngredient == null) {
            mainCtrl.showError("Select an ingredient to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Ingredient?");
        confirm.setContentText("Are you sure you want to remove this ingredient?");

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            Recipe updatedRecipe =
                    server.deleteRecipeIngredient(selectedRecipe, selectedRecipeIngredient);
            int index = data.indexOf(selectedRecipe);
            if (index != -1) {
                data.set(index, updatedRecipe);
            }
            selectRecipe(updatedRecipe);
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }

        server.deleteRecipeIngredient(
                selectedRecipeIngredient.getRecipe(), selectedRecipeIngredient
        );
        tableIngredients.getItems().remove(selectedRecipeIngredient);
    }

    /**
     * Makes every component except the label invisible
     * Sets the label text to "Welcome to FoodPal!"
     */
    public void showMainMenu() {
        recipeEditButton.setVisible(false);
        recipeName.setText("Welcome to FoodPal!");
        tableIngredients.setVisible(false);
        tablePreparation.setVisible(false);
        recipeIngredientAdd.setVisible(false);
        recipeIngredientDelete.setVisible(false);
        downloadRecipeButton.setVisible(false);
        editStepsButton.setVisible(false);
        removeStepButton.setVisible(false);
        addRecipeStep.setVisible(false);
    }

    /**
     * Makes every component that the user should
     * be able to interact with when a recipe is selected visible
     */
    public void loadRecipeOverviewUI() {
        tableIngredients.setVisible(true);
        tablePreparation.setVisible(true);

        recipeEditButton.setVisible(true);
        recipeName.setVisible(true);

        recipeIngredientAdd.setVisible(true);
        recipeIngredientDelete.setVisible(true);

        downloadRecipeButton.setVisible(true);
        editStepsButton.setVisible(true);
        removeStepButton.setVisible(true);
        addRecipeStep.setVisible(true);
    }

    /**
     * Opens the Ingredient Overview scene
     */
    public void showIngredients() {
        mainCtrl.showIngredientsOverview();
    }

    /**
     * Opens the ingredient chooser for the currently selected recipe.
     * Shows an error if no recipe is selected.
     */
    @FXML
    private void openAddRecipeIngredient() {
        var selected = tableRecipes.getSelectionModel().getSelectedItem();
        if (selected == null) { mainCtrl.showError("Select a recipe first."); return; }
        mainCtrl.showChooseRecipeIngredient(selected);
    }

    /**
     * Opens the “Add Recipe Step” screen for the selected recipe.
     * Shows an error if no recipe is selected.
     */
    @FXML
    private void openAddRecipeStep() {
        var selected = tableRecipes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mainCtrl.showError("Select a recipe first.");
            return;
        }
        mainCtrl.showAddRecipeStep(selected);
    }

    /**
     * Returns a nicely formatted string representation of a {@link RecipeStep}.
     * Example: "1. Chop onions" or just "Chop onions" if position is invalid.
     *
     * @param step the recipe step to format
     * @return formatted string for display
     */
    private String formatStepForDisplay(RecipeStep step) {
        if (step == null) {
            return "";
        }

        String number = "";
        if (step.getPosition() >= 0) {
            number = String.valueOf(step.getPosition());
        }

        String text = "";
        if (step.getInstruction() != null) {
            text = step.getInstruction();
        }

        if (!number.isEmpty()) {
            return (number + ". " + text).trim();
        }
        return text.trim();
    }

    /**
     * Deletes the selected step and automatically shifts subsequent steps up by one.
     */
    @FXML
    public void removeStep() {
        Recipe selectedRecipe = tableRecipes.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            mainCtrl.showError("Select a recipe first.");
            return;
        }

        RecipeStep selectedStep = tablePreparation.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            mainCtrl.showError("Select a step to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Step");
        confirm.setContentText("Are you sure you want to delete step "
                + selectedStep.getPosition() + "?");

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            // Delete the selected step
            server.deleteRecipeStep(selectedRecipe.getId(), selectedStep.getId());

            // Fetch remaining steps and shift positions
            List<RecipeStep> steps = server.getStepsForRecipe(selectedRecipe.getId());
            int deletedPos = selectedStep.getPosition();

            for (RecipeStep step : steps) {
                if (step.getPosition() > deletedPos) {
                    step.setPosition(step.getPosition() - 1);
                    server.updateRecipeStep(selectedRecipe.getId(), step);
                }
            }

            // Reload the step table
            loadStepsForRecipe(selectedRecipe);

        } catch (Exception e) {
            mainCtrl.showError("Failed to delete step: " + e.getMessage());
        }
    }

    /**
     * Downloads the recipe as a file. This file is in markdown.
     * It will first open a file explorer so the user can set a name and location of the file.
     * When no recipe is selected, an error window will pop up.
     */
    @FXML
    private void downloadRecipe() {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mainCtrl.showError("Select a recipe first.");
            return;
        }

        String content = RecipeFormatter.format(selected);

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Recipe");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Markdown file (*.md)", "*.md")
        );

        chooser.setInitialFileName(
                selected.getTitle().replaceAll("\\s+", "_") + ".md"
        );

        File file = chooser.showSaveDialog(
                tableRecipes.getScene().getWindow()
        );

        if (file == null) {
            return; // user cancelled
        }

        try {
            Files.writeString(file.toPath(), content);
        } catch (IOException e) {
            mainCtrl.showError("Failed to save recipe.");
        }
    }

}