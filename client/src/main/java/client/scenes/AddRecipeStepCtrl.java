package client.scenes;

import client.utils.ServerUtils;
import commons.Recipe;
import commons.RecipeStep;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.List;
import com.google.inject.Inject;
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
     * AddRecipeStepCtrl constructor
     * @param server serverUtils connection
     * @param mainCtrl mainCtrl reference
     */
    @Inject
    public AddRecipeStepCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Sets the recipe context for which a new step will be created.
     * Displays the next auto-assigned step number (read-only).
     *
     * @param recipe target recipe
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        this.editingStep = null;
        this.originalPosition = null;

        // Clear UI when opening add screen
        if (instructionInput != null) instructionInput.clear();

        if (positionInput != null) {
            // Always show position but do not allow editing
            positionInput.setEditable(false);
            positionInput.setDisable(true);

            // Compute and display next available step number
            if (recipe == null || recipe.getId() == null) {
                positionInput.setText(""); // cannot compute without recipe id
                positionInput.setPromptText("Auto");
                return;
            }

            try {
                List<RecipeStep> existingSteps = server.getStepsForRecipe(recipe.getId());
                int next = chooseAutoPosition(existingSteps);
                positionInput.setText(String.valueOf(next));
            } catch (WebApplicationException e) {
                // If backend fails, still show something sensible
                positionInput.setText("");
                positionInput.setPromptText("Auto");
                mainCtrl.showExceptionErrorPopUp(e);
            }
        }
    }

    /**
     * Sets the context for editing an existing step.
     * Displays the current step number (read-only) and prefills instruction text.
     *
     * @param recipe the recipe that the preparation step belongs to
     * @param step   the step that is being edited
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
        instructionInput.setText(instruction == null ? "" : instruction);

        // Prefill + lock position (read-only display)
        if (positionInput != null) {
            positionInput.setEditable(false);
            positionInput.setDisable(true);

            Integer position = step.getPosition();
            positionInput.setText(position == null ? "" : String.valueOf(position));
        }
    }

    /**
     * Cancels and returns to the overview.
     */
    @FXML
    public void cancel() {
        mainCtrl.showRecipeOverview();
    }

    /**
     * Chooses an automatic step position.
     * Uses the smallest positive integer not already used by an existing step.
     *
     * @param steps the existing steps for the recipe
     * @return the first free position starting at 1
     */
    private static int chooseAutoPosition(List<RecipeStep> steps) {
        int pos = 1;
        if (steps == null) {
            return pos;
        }

        boolean used;
        do {
            used = false;
            for (RecipeStep s : steps) {
                if (s != null && s.getPosition() == pos) {
                    used = true;
                    pos++;
                    break;
                }
            }
        } while (used);

        return pos;
    }

    //AI-generated
    /**
     * Validates input, computes position (append if empty),
     * POSTs the new step, refreshes the overview, and navigates back.
     */
    @FXML
    public void ok() {
        if (!validateRecipeSelected()) {
            return;
        }

        String instruction = readAndValidateInstruction();
        if (instruction == null) {
            return;
        }

        // Always fetch latest steps once
        List<RecipeStep> existingSteps = fetchExistingStepsOrShowError();
        if (existingSteps == null) {
            return;
        }

        int position;

        if (editingStep == null) {
            // ADD MODE: client assigns next available number, user cannot influence it
            position = chooseAutoPosition(existingSteps);
            positionInput.setText(String.valueOf(position)); // keep UI consistent
        } else {
            // EDIT MODE: keep the existing number
            Integer current = editingStep.getPosition();
            if (current == null || current < 1) {
                mainCtrl.showError("Invalid step number on selected step.");
                return;
            }
            position = current;
        }

        // Safety check
        if (!validateNoPositionConflict(existingSteps, position)) {
            return;
        }

        if (!persistStepOrShowError(instruction, position)) {
            return;
        }

        refreshOverview();
        clearInputs();
        mainCtrl.showRecipeOverview();
    }

    /**
     * Ensures a recipe is selected; otherwise shows an error.
     *
     * @return true if a recipe is selected; false otherwise.
     */
    private boolean validateRecipeSelected() {
        if (recipe == null) {
            mainCtrl.showError("No recipe selected.");
            return false;
        }
        return true;
    }

    /**
     * Reads and validates the instruction input (trimmed and non-empty);
     * otherwise shows an error.
     *
     * @return trimmed instruction, or null if invalid.
     */
    private String readAndValidateInstruction() {
        String instructionRaw = instructionInput.getText();
        if (instructionRaw == null || instructionRaw.trim().isEmpty()) {
            mainCtrl.showError("Instruction cannot be empty.");
            return null;
        }
        return instructionRaw.trim();
    }


    /**
     * Fetches existing steps for the selected recipe; otherwise shows an error popup.
     *
     * @return list of existing steps, or null if the request failed.
     */
    private List<RecipeStep> fetchExistingStepsOrShowError() {
        try {
            return server.getStepsForRecipe(recipe.getId());
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return null;
        }
    }

    /**
     * Validates that no other step already uses the given position, ignoring the step
     * currently being edited; otherwise shows an error.
     *
     * @param existingSteps current steps for the recipe
     * @param position      desired position
     * @return true if no conflict exists; false otherwise.
     */
    private boolean validateNoPositionConflict(List<RecipeStep> existingSteps, int position) {
        if (existingSteps == null) {
            return true;
        }

        for (RecipeStep s : existingSteps) {
            if (s == null) continue;

            Integer sp = s.getPosition();
            boolean samePosition = sp != 0 && sp.equals(position);
            if (!samePosition) continue;

            // If we are editing, allow keeping the same position for the same step
            if (editingStep != null) {
                if (s.getId() != null && editingStep.getId() != null
                        && s.getId().equals(editingStep.getId())) {
                    continue; // it's the same step -> not a conflict
                }
            }

            mainCtrl.showError("A step with number " + position + " already exists.");
            return false;
        }

        return true;
    }

    /**
     * Persists the step via the server as either an add or an update
     *
     * @param instruction validated instruction
     * @param position    final position
     * @return true if the request succeeded; false otherwise.
     */
    private boolean persistStepOrShowError(String instruction, int position) {
        try {
            if (editingStep == null) {
                // ADD
                RecipeStep step = new RecipeStep();
                step.setInstruction(instruction);
                step.setPosition(position);
                server.addRecipeStep(recipe.getId(), step);
            } else {
                // EDIT
                RecipeStep updated = new RecipeStep();
                updated.setId(editingStep.getId());
                updated.setInstruction(instruction);
                updated.setPosition(position);
                server.updateRecipeStep(recipe.getId(), updated);
            }
            return true;
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return false;
        }
    }

    /**
     * Refreshes the overview controller to keep the selected recipe and reload its steps.
     */
    private void refreshOverview() {
        var overview = mainCtrl.getRecipeOverviewCtrl();
        if (overview != null) {
            overview.refresh();                  // ensure recipes list is up to date
            overview.selectRecipe(recipe);       // keep selection
            overview.loadStepsForRecipe(recipe); // reload steps table
        }
    }

    /**
     * Clears the instruction and position input fields.
     */
    private void clearInputs() {
        instructionInput.clear();
        positionInput.clear();
    }
}