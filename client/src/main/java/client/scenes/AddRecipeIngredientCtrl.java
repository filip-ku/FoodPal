package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class AddRecipeIngredientCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Recipe recipe;

    @FXML
    private TextField ingredientNameInput;
    @FXML
    private TextField quantityInput;
    @FXML
    private TextField unitsInput;
    @FXML
    private TextField notesInput;

    /**
     * Creates a controller with injected dependencies.
     *
     * @param server  utility for communicating with the backend.
     * @param mainCtrl reference to the main UI controller.
     */
    @Inject
    public AddRecipeIngredientCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Cancels the dialog and returns to the recipe overview.
     */
    public void cancel() {
        clearFields();
        mainCtrl.showRecipeOverview();
    }

    /**
     * Attempts to submit a new RecipeIngredient; shows an error alert if it fails.
     */
    public void ok() {
        Ingredient ingredient = getIngredient();
        int quantity;
        String units;
        String notes;

        try {
            quantity = getQuantity();
        } catch (NumberFormatException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Quantity must be a valid number.");
            alert.showAndWait();
            return;
        }
        units = getUnitsInput();
        notes = getNotesInput();

        // TODO: add ingredient to the database

        // TODO: add ingredient to the overview table
    
        // ensure the recipe is selected so the ingredient table is visible
        mainCtrl.getRecipeOverviewCtrl().selectRecipe(recipe);

        // clear the add form and show the overview
        clearFields();
        mainCtrl.showRecipeOverview();
    }

    private int getQuantity() throws NumberFormatException {
        String text = quantityInput.getText().trim();
        if (text.isEmpty()) throw new NumberFormatException("Quantity is empty");
        return Integer.parseInt(text);
    }

    private Ingredient getIngredient() {
        return new Ingredient(ingredientNameInput.getText());
    }

    public String getUnitsInput() {
        return unitsInput.getText();
    }

    public String getNotesInput() {
        return notesInput.getText();
    }

    public void setRecipe(Recipe recipe) {this.recipe = recipe;}

    private void clearFields() {
        ingredientNameInput.clear();
        quantityInput.clear();
        unitsInput.clear();
        notesInput.clear();

    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }

}