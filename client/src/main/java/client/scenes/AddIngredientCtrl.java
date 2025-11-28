package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class AddIngredientCtrl {

    private final ServerUtils server;
    private final MainCtrl MainCtrl;
    private Recipe recipe;

    @FXML
    private TextField ingredientNameInput;
    @FXML
    private TextField quantityInput;
    @FXML
    private TextField unitsInput;
    @FXML
    private TextField notesInput;

    @Inject
    public AddIngredientCtrl(ServerUtils server, MainCtrl MainCtrl) {
        this.MainCtrl = MainCtrl;
        this.server = server;
    }
    public void cancel() {
        clearFields();
        MainCtrl.showRecipeOverview();
    }


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

        RecipeIngredient newIngredient = new RecipeIngredient(ingredient, quantity, units, notes);

        // add ingredient to the overview table
        MainCtrl.getRecipeOverviewCtrl()
                .addIngredientToTable(ingredient, quantity, units, notes);
    
        // ensure the recipe is selected so the ingredient table is visible
        MainCtrl.getRecipeOverviewCtrl().selectRecipe(recipe);

        // clear the add form and show the overview
        clearFields();
        MainCtrl.showRecipeOverview();
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