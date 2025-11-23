package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class AddRecipeController {

    private final ServerUtils server;
    private final MainController mainController;

    @FXML
    private TextField title;

    @Inject
    public AddRecipeController(ServerUtils server, MainController mainController) {
        this.mainController = mainController;
        this.server = server;
    }
    public void cancel() {
        clearFields();
        mainController.showRecipeOverview();
    }

    public void ok() {
        try {
            server.addRecipe(getRecipe());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainController.showRecipeOverview();
    }

    private Recipe getRecipe() {
        return new Recipe(title.getText());
    }

    private void clearFields() {
        title.clear();
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
