package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Ingredient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class IngredientsOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<Ingredient> data;

    @FXML
    private TableView<Ingredient> tableIngredients;
    @FXML
    private TableColumn<Ingredient, String> colName;
    @FXML
    private TableColumn<Ingredient, Integer> colNumOfRecipes;

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
     * “main menu” view.</p>
     *
     * @param location  location of the FXML file (unused)
     * @param resources resource bundle for internationalisation (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getName()));

        // TODO: Show how many recipes an ingredient is used in
    }

    /**
     * Refreshes the recipe list from the server and updates the table view.
     */
    public void refresh() {
        var ingredients = server.getIngredients();
        data = FXCollections.observableList(ingredients);
        tableIngredients.setItems(data);
    }

    /**
     * Navigates to the “Add Ingredient” screen.
     */
    public void addGlobalIngredient() {
        mainCtrl.showAddIngredient();
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

        if (selected == null) {
            mainCtrl.showError("No ingredient selected.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Ingredient?");
        confirm.setContentText("You sure you wanna delete this ingredient?");

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        server.deleteIngredient(selected);

        data.remove(selected);
    }
}
