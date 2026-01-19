package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the "Add Recipe" dialog.
 */
public class AddRecipeCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField title;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private TextField servings;

    @FXML
    private ResourceBundle resources;

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
     * Initializes the language dropdown with supported languages.
     *
     * @param location location of the FXML file (unused)
     * @param resources resource bundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        ObservableList<String> languageOptions = FXCollections.observableArrayList(
                resources.getString("addRecipe.language.english"),
                resources.getString("addRecipe.language.dutch"),
                resources.getString("addRecipe.language.spanish"),
                resources.getString("addRecipe.language.french")
        );
        languageComboBox.setItems(languageOptions);
        languageComboBox.getSelectionModel().selectFirst(); // Default to first language
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
     * Creates a {@link Recipe} from the current title field and selected language.
     *
     * @return new recipe instance
     */
    private Recipe getRecipe() {
        Recipe recipe = new Recipe(title.getText());
        String selectedLanguageName = languageComboBox.getSelectionModel().getSelectedItem();

        try {
            int servingsValue = Integer.parseInt(servings.getText());
            recipe.setServings(BigDecimal.valueOf(servingsValue));
        }  catch (NumberFormatException e) {
            if (servings.getText().isBlank()) {
                mainCtrl.showError(resources.getString("addRecipe.error.servingsBlank"));
            } else {
                mainCtrl.showError(resources.getString("addRecipe.error.servingsNumber"));
            }
            return null;
        }

        if (selectedLanguageName != null) {
            // Map display name to language code
            String languageCode = mapLanguageNameToCode(selectedLanguageName);
            recipe.setLanguage(languageCode);
        }
        return recipe;
    }

    /**
     * Maps language display name to language code.
     *
     * @param languageName the display name
     * @return the language code (e.g., "en", "nl", "es")
     */
    private String mapLanguageNameToCode(String languageName) {
        if (languageName.equals(resources.getString("addRecipe.language.english"))) {
            return "en";
        } else if (languageName.equals(resources.getString("addRecipe.language.dutch"))) {
            return "nl";
        } else if (languageName.equals(resources.getString("addRecipe.language.spanish"))) {
            return "es";
        } else if (languageName.equals(resources.getString("addRecipe.language.french"))) {
            return "fr";
        }
        return "en"; // Default to English
    }

    /**
     * Clears all input fields in this dialog.
     */
    private void clearFields() {
        title.clear();
        languageComboBox.getSelectionModel().selectFirst();
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
