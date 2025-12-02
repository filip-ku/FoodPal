package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * Controller for the "Add Recipe" dialog.
 */
public class AddRecipeCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField title;

    /**
     * Creates a controller with injected dependencies.
     *
     * @param server  utility for communicating with the backend.
     * @param mainCtrl reference to the main UI controller.
     */
    @Inject
    public AddRecipeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Cancels the dialog and returns to the recipe overview.
     */
    public void cancelButton() {
        clearFields();
        mainCtrl.showRecipeOverview();
    }

    /**
     * Attempts to submit a new recipe; shows an error alert if it fails.
     */
    public void okButton() {
        try {
            server.addRecipe(getRecipe());
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return;
        }

        clearFields();
        mainCtrl.showRecipeOverview();
    }

    /**
     * Creates a {@link Recipe} from the current title field.
     *
     * @return new recipe instance
     */
    private Recipe getRecipe() {
        return new Recipe(title.getText());
    }

    /**
     * Clears all input fields in this dialog.
     */
    private void clearFields() {
        title.clear();
    }

    /**
     * Handles key presses: ENTER submits, ESCAPE cancels.
     *
     * @param e key event to process
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                okButton();
                break;
            case ESCAPE:
                cancelButton();
                break;
            default:
                break;
        }
    }

}
