package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class AddRecipeIngredientCtrl {

    // Differentiating between adding and editing an ingredient
    public enum Mode { ADD, EDIT }

    private Mode mode = Mode.ADD;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Recipe recipe;

    private Ingredient ingredient;
    private RecipeIngredient existing;     // non-null only in EDIT mode

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
     * @param server utility for communicating with the backend.
     * @param mainCtrl reference to the main UI controller.
     */
    @Inject
    public AddRecipeIngredientCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    // AI-generated
    /**
     * Configures the screen to ADD a new recipe ingredient.
     * Prefills the ingredient name and clears the editable fields.
     * @param recipe parent recipe
     * @param ingredient chosen global ingredient
     */
    public void setContextForAdd(Recipe recipe, Ingredient ingredient) {
        this.mode = Mode.ADD;
        this.recipe = recipe;
        this.ingredient = ingredient;
        prefill(null, ingredient);
    }

    // AI-generated
    /**
     * Configures the screen to EDIT an existing recipe ingredient.
     * Prefills all fields with existing values.
     * @param recipe parent recipe
     * @param ri existing recipe-ingredient row
     * @param ingredient global ingredient referenced by {@code ri}
     */
    public void setContextForEdit(Recipe recipe, RecipeIngredient ri, Ingredient ingredient) {
        this.mode = Mode.EDIT;
        this.recipe = recipe;
        this.existing = ri;
        this.ingredient = ingredient;
        prefill(ri, ingredient);
    }

    // AI-generated
    /**
     * Prefills the UI fields for ADD or EDIT.
     * The ingredient name is always shown but locked (non-editable).
     * Other fields are either cleared (ADD mode) or populated (EDIT mode).
     *
     * @param ri existing RecipeIngredient when editing, or {@code null} when adding
     * @param ing global Ingredient to display
     */
    private void prefill(RecipeIngredient ri, Ingredient ing) {
        ingredientNameInput.setText(ing.getName());
        ingredientNameInput.setEditable(false);
        ingredientNameInput.setDisable(true);

        if (ri == null) {
            quantityInput.clear();
            unitsInput.clear();
            notesInput.clear();
        } else {
            if (ri.getAmount() == null) {
                quantityInput.clear();
            } else {
                quantityInput.setText(ri.getAmount().toString());
            }

            if (ri.getUnit() == null) {
                unitsInput.clear();
            } else {
                unitsInput.setText(ri.getUnit());
            }

            if (ri.getInformalAmount() == null) {
                notesInput.clear();
            } else {
                notesInput.setText(ri.getInformalAmount());
            }
        }
    }

    /**
     * Converts the quantity text into a Double value.
     * Returns {@code null} if the input is blank, to allow informal-only entries.
     *
     * @param text the quantity text entered by the user
     * @return the numeric value as a Double, or {@code null} if left empty
     */
    private static Double parseAmount(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return Double.parseDouble(trimmed);
    }

    /**
     * Converts an empty or whitespace-only string to {@code null}.
     * Returns a trimmed version otherwise.
     *
     * @param s input text
     * @return {@code null} if blank, or trimmed string otherwise
     */
    private static String emptyToNull(String s) {
        if (s == null) {
            return null;
        }

        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        return trimmed;
    }

    /**
     * Cancels the dialog and returns to the recipe overview.
     */
    public void cancel() {
        clearFields();
        mainCtrl.showRecipeOverview();
    }

    /**
     * Validates input and saves changes:
     *  - ADD: creates a new RecipeIngredient via POST.
     *  - EDIT: updates the existing RecipeIngredient via PUT.
     * On success, returns to the recipe overview.
     */
    public void ok() {
        final Double amount;
        try {
            amount = parseAmount(quantityInput.getText());
            if (amount != null && amount < 0) {
                throw new NumberFormatException("Quantity cannot be negative.");
            }
        } catch (NumberFormatException e) {
            mainCtrl.showError(e.getMessage());
            return;
        }

        final String unit = emptyToNull(unitsInput.getText());
        final String informal = emptyToNull(notesInput.getText());

        boolean hasAmount = amount != null && amount != 0;
        boolean hasUnit = unit != null;
        boolean hasInformal = informal != null && !informal.isBlank();

        if (hasAmount != hasUnit) {
            mainCtrl.showError("Amount and unit must both be filled together.");
            return;
        }

        boolean hasAmountAndUnit = hasAmount && hasUnit;
        if (!(hasAmountAndUnit ^ hasInformal)) {
            mainCtrl.showError("Please fill in either amount and unit OR informal amount.");
            return;
        }
        try{
            if (mode == Mode.ADD) {
                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipe(recipe);
                ri.setIngredient(ingredient);
                ri.setAmount(amount);
                ri.setUnit(unit);
                ri.setInformalAmount(informal);

                boolean riAlreadyExists = recipe.getIngredients().stream()
                        .anyMatch(existingRi -> existingRi.getIngredient().equals(ingredient));

                if (riAlreadyExists) {
                    mainCtrl.showError("This ingredient is already in the recipe.");
                    return;
                }

                server.addRecipeIngredient(recipe, ri);
            } else {
                existing.setAmount(amount);
                existing.setUnit(unit);
                existing.setInformalAmount(informal);
                server.updateRecipeIngredient(recipe, existing);
            }

            if (mainCtrl.getRecipeOverviewCtrl() != null) {
                mainCtrl.getRecipeOverviewCtrl().refresh();
                mainCtrl.getRecipeOverviewCtrl().selectRecipe(recipe);
            }
            mainCtrl.showRecipeOverview();
            clearFields();
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
        }
    }

    /**
     * Clears all editable input fields.
     * Used when leaving the screen.
     */
    public void clearFields() {
        ingredientNameInput.clear();
        quantityInput.clear();
        unitsInput.clear();
        notesInput.clear();
    }

}