package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeStep;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Screen for creating a new recipe step.
 * Lets the user enter instruction text and (optionally) a position.
 */
public class AddRecipeStepCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Recipe recipe;

    @FXML private TextField positionInput;   // optional; leave empty to append
    @FXML private TextArea instructionInput;

    /**
     * Constructs the controller with injected dependencies.
     * @param server   server utilities for backend calls
     * @param mainCtrl main navigation controller
     */
    @Inject
    public AddRecipeStepCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Sets the recipe context for which a new step will be created.
     * @param recipe target recipe
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Cancels and returns to the overview.
     */
    @FXML
    public void cancel() {
        mainCtrl.showRecipeOverview();
    }

    //AI-generated
    /**
     * Validates input, computes position (append if empty),
     * POSTs the new step, refreshes the overview, and navigates back.
     */
    @FXML
    public void ok() {
        if (recipe == null) {
            mainCtrl.showError("No recipe selected.");
            return;
        }

        // 1) Validate instruction
        String instructionRaw = instructionInput.getText();
        if (instructionRaw == null || instructionRaw.trim().isEmpty()) {
            mainCtrl.showError("Instruction cannot be empty.");
            return;
        }
        String instruction = instructionRaw.trim();

        // 2) Determine position
        Integer position = null;
        String posText = positionInput.getText();
        if (posText != null) {
            String t = posText.trim();
            if (!t.isEmpty()) {
                try {
                    int p = Integer.parseInt(t);
                    if (p < 1) {
                        mainCtrl.showError("Position must be a non-zero integer.");
                        return;
                    }
                    position = p;
                } catch (NumberFormatException ex) {
                    mainCtrl.showError("Position must be an integer.");
                    return;
                }
            }
        }

        // If position not provided, append at the end
        if (position == null) {
            List<RecipeStep> existing = server.getStepsForRecipe(recipe.getId());
            int next = 1;
            if (existing != null) {
                next = existing.size() + 1;
            }
            position = next;
        }

        // In case of duplicate step numbers, gives an error
        List<RecipeStep> existingSteps = server.getStepsForRecipe(recipe.getId());
        if (existingSteps != null) {
            for (RecipeStep s : existingSteps) {
                if (s != null && s.getPosition() == position) {
                    mainCtrl.showError("A step with number " + position + " already exists.");
                    return;
                }
            }
        }

        // 3) Create recipeStep object
        RecipeStep step = new RecipeStep();
        step.setInstruction(instruction);
        step.setPosition(position);

        // 4) Persist on server
        server.addRecipeStep(recipe.getId(), step);

        // 5) Refresh overview and navigate back
        var overview = mainCtrl.getRecipeOverviewCtrl();
        if (overview != null) {
            overview.refresh();             // ensure recipes list is up to date
            overview.selectRecipe(recipe);  // keep selection
            overview.loadStepsForRecipe(recipe); // reload steps table
        }

        // clearing text fields
        instructionInput.clear();
        positionInput.clear();

        mainCtrl.showRecipeOverview();
    }
}