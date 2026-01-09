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
        // 0) Validate recipe
        if (!validateRecipeSelected()) {
            return;
        }

        // 1) Validate instruction
        String instruction = readAndValidateInstruction();
        if (instruction == null) {
            return;
        }

        // 2) Determine position (optional)
        Integer position = readAndValidatePosition();
        if (position == INVALID_POSITION) {
            return;
        }

        // 3) Fetch existing steps
        List<RecipeStep> existingSteps = fetchExistingStepsOrShowError();
        if (existingSteps == null) {
            return;
        }

        // 4) If position not provided, choose a free position
        if (position == null) {
            position = chooseAutoPosition(existingSteps);
        }

        // 5) Client-side precheck, ignore the step being edited.
        if (!validateNoPositionConflict(existingSteps, position)) {
            return;
        }

        // 6) Persist on server (add or edit)
        if (!persistStepOrShowError(instruction, position)) {
            return;
        }

        // 7) Refresh overview, clear fields, and navigate back
        refreshOverview();
        clearInputs();
        mainCtrl.showRecipeOverview();
    }

    private static final Integer INVALID_POSITION = Integer.MIN_VALUE;

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
     * Reads and validates the optional position input; if empty returns null,
     * and if invalid shows an error.
     *
     * @return parsed position, null if not provided, or INVALID_POSITION if invalid.
     */
    private Integer readAndValidatePosition() {
        String posText = positionInput.getText();
        if (posText == null) {
            return null;
        }

        String t = posText.trim();
        if (t.isEmpty()) {
            return null;
        }

        try {
            int p = Integer.parseInt(t);
            if (p < 1) {
                mainCtrl.showError("Position must be a non-zero integer.");
                return INVALID_POSITION;
            }
            return p;
        } catch (NumberFormatException ex) {
            mainCtrl.showError("Position must be an integer.");
            return INVALID_POSITION;
        }
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

            boolean samePosition = s.getPosition() == position;
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