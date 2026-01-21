package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class ChooseRecipeIngredientCtrl {

    @FXML
    private ComboBox<Ingredient> ingredientSelect;

    @FXML
    private Button nextButton;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Recipe recipe;

    @FXML
    private java.util.ResourceBundle resources;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param server used for backend communication
     * @param mainCtrl navigation hub for switching scenes
     */
    @Inject
    public ChooseRecipeIngredientCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes UI bindings for this scene.
     * Disables the Next button until an ingredient is selected.
     */
    @FXML
    public void initialize() {
        nextButton.disableProperty()
                .bind(ingredientSelect.getSelectionModel().selectedItemProperty().isNull());
    }

    /**
     * Supplies the current recipe context and populates the dropdown
     * with all available ingredients from the server.
     *
     * @param recipe the recipe the user is editing
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        ingredientSelect.getItems().setAll(server.getIngredients());
        ingredientSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(Ingredient i) {
                return i == null ? "" : i.getName();
            }

            @Override
            public Ingredient fromString(String s) {
                return null;
            }
        });
    }

    /**
     * Opens the “Add Ingredient” screen to create a new global ingredient,
     * then refreshes the dropdown list after returning.
     */
    public void openAddIngredient() {
        mainCtrl.showAddIngredient();
        ingredientSelect.getItems().setAll(server.getIngredients());
    }

    /**
     * Returns to the recipe overview without making changes.
     */
    public void cancel() {
        mainCtrl.showRecipeOverview();
    }

    /**
     * Proceeds to the details screen to input recipe-specific data
     * (amount, unit, informal amount) for the selected ingredient.
     * Shows an error if no ingredient is selected.
     */
    public void inputRecipeSpecificData() {
        var selected = ingredientSelect.getValue();
        if (selected == null) {
            mainCtrl.showError(resources.getString("chooseRecipeIngredient.error.noSelection"));
            return;
        }
        mainCtrl.showAddRecipeIngredientForAdd(recipe, selected);
    }
}