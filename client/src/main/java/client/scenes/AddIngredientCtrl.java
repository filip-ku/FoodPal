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

public class AddIngredientCtrl {

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
     * Creates a new AddIngredientCtrl with the given dependencies.
     * @param server handles communication with the backend server
     * @param mainCtrl manages navigation and access to other controllers
     */
    @Inject
    public AddIngredientCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }
    /**
     * Cancels the ingredient addition process.
     * Clears all input fields and returns to the recipe overview screen.
     */
    public void cancel() {
        clearFields();
        mainCtrl.showRecipeOverview();
    }

    /**
     * Confirms and saves a new ingredient entry.
     * Validates the quantity input, creates an Ingredient object,
     * and adds it to the current recipe's ingredient table.
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

        // add ingredient to the overview table
        mainCtrl.getRecipeOverviewCtrl()
                .addIngredientToTable(ingredient, quantity, units, notes);
    
        // ensure the recipe is selected so the ingredient table is visible
        mainCtrl.getRecipeOverviewCtrl().selectRecipe(recipe);

        // clear the add form and show the overview
        clearFields();
        mainCtrl.showRecipeOverview();
    }

    /**
     * Parses and returns the quantity entered by the user.
     *
     * @return the quantity as an integer
     * @throws NumberFormatException if the field is empty or contains invalid text
     */
    private int getQuantity() throws NumberFormatException {
        String text = quantityInput.getText().trim();
        if (text.isEmpty()) throw new NumberFormatException("Quantity is empty");
        return Integer.parseInt(text);
    }

    /**
     * Creates a new Ingredient instance using the entered ingredient name.
     *
     * @return a new {@link Ingredient} object based on user input
     */
    private Ingredient getIngredient() {
        return new Ingredient(ingredientNameInput.getText());
    }

    /**
     * Retrieves the text from the "units" input field.
     * @return the unit string entered by the user
     */
    private String getUnitsInput() {
        return unitsInput.getText();
    }

    /**
     * Retrieves the text from the "notes" input field.
     * @return the note string entered by the user
     */
    private String getNotesInput() {
        return notesInput.getText();
    }

    public void setRecipe(Recipe recipe) {this.recipe = recipe;}

    /**
     * Clears all input text fields in the add ingredient form.
     * Used when cancelling or after successfully adding an ingredient.
     */
    private void clearFields() {
        ingredientNameInput.clear();
        quantityInput.clear();
        unitsInput.clear();
        notesInput.clear();
    }

    /**
     * Handles keyboard input events for this scene.
     * Pressing ENTER submits the ingredient, while ESCAPE cancels the action.
     * @param e the keyboard event triggered by the user
     */
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