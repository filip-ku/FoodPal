package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class AddIngredientCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField ingredientNameInput;

    /**
     * Creates a controller with injected dependencies.
     *
     * @param server  utility for communicating with the backend.
     * @param mainCtrl reference to the main UI controller.
     */
    @Inject
    public AddIngredientCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Cancels the dialog and returns to the ingredient overview.
     */
    public void cancelButton() {
        clearFields();
        mainCtrl.showIngredientsOverview();
    }

    /**
     * Attempts to submit a new ingredient; shows an error alert if it fails.
     */
    public void okButton() {
        try {
            server.addIngredient(getIngredient());
        } catch (WebApplicationException e) {
            mainCtrl.showExceptionErrorPopUp(e);
            return;
        }

        mainCtrl.showIngredientsOverview();
        clearFields();
    }

    /**
     * Creates a {@link Ingredient} from the current title field.
     *
     * @return new ingredient instance
     */
    private Ingredient getIngredient() {
        return new Ingredient(ingredientNameInput.getText());
    }

    private void clearFields() {
        ingredientNameInput.clear();
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