package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeStep;
import jakarta.ws.rs.WebApplicationException;
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

    private RecipeStep editingStep;     // null if adding
    private Integer originalPosition;   // for detecting position changes

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
        this.editingStep = null;
        this.originalPosition = null;

        // clear UI when opening add screen
        if (instructionInput != null) instructionInput.clear();
        if (positionInput != null) positionInput.clear();
    }

    /**
     * Cancels and returns to the overview.
     */
    @FXML
    public void cancel() {
        mainCtrl.showRecipeOverview();
    }

    /**
     * Sets the context for editing an existing step.
     * Prefills UI fields with existing values.
     * @param recipe the recipe that the preparation step belongs to
     * @param step the step that is being edited
     */
    public void setContextForEdit(Recipe recipe, RecipeStep step) {
        if (recipe == null) {
            throw new IllegalArgumentException("recipe must not be null");
        }
        if (step == null) {
            throw new IllegalArgumentException("step must not be null");
        }

        this.recipe = recipe;
        this.editingStep = step;
        this.originalPosition = step.getPosition();

        // Prefill instruction
        String instruction = step.getInstruction();
        if (instruction == null) {
            instructionInput.setText("");
        } else {
            instructionInput.setText(instruction);
        }

        // Prefill position
        Integer position = step.getPosition();
        if (position == null) {
            positionInput.setText("");
        } else {
            positionInput.setText(String.valueOf(position));
        }
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

        // Client-side precheck, ignore the step being edited.
        List<RecipeStep> existingSteps = server.getStepsForRecipe(recipe.getId());
        if (existingSteps != null) {
            for (RecipeStep s : existingSteps) {
                if (s == null) continue;

                boolean samePosition = s.getPosition() == position;
                if (!samePosition) continue;

                // If we are editing, allow keeping the same position for the same step
                if (editingStep != null) {
                    if (s.getId() != null && editingStep.getId()
                            != null && s.getId().equals(editingStep.getId())) {
                        continue; // it's the same step -> not a conflict
                    }
                }

                mainCtrl.showError("A step with number " + position + " already exists.");
                return;
            }
        }

        // 3) Create recipeStep object
        RecipeStep step = new RecipeStep();
        step.setInstruction(instruction);
        step.setPosition(position);

        // 4) Persist on server
        try {
            if (editingStep == null) {
                // ADD
                server.addRecipeStep(recipe.getId(), step);
            } else {
                // EDIT
                RecipeStep updated = new RecipeStep();
                updated.setId(editingStep.getId());
                updated.setInstruction(instruction);
                updated.setPosition(position);

                server.updateRecipeStep(recipe.getId(), updated);
            }
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return;
        }

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