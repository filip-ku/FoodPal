package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AddIngredientCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField ingredientNameInput;
    @FXML
    private TextField proteinInput;
    @FXML
    private TextField fatInput;
    @FXML
    private TextField carbsInput;

    @FXML
    private Label kcalLabel;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        proteinInput.textProperty().addListener((obs, old, nw) -> updateKcalLabel());
        fatInput.textProperty().addListener((obs, old, nw) -> updateKcalLabel());
        carbsInput.textProperty().addListener((obs, old, nw) -> updateKcalLabel());

        updateKcalLabel();
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
        Ingredient newIngredient = getIngredient();

        if (newIngredient == null) return;

        try {
            boolean nameExists = server.getIngredients().stream()
                    .anyMatch(existing ->
                            existing.getName().equalsIgnoreCase(newIngredient.getName()));

            if (nameExists) {
                mainCtrl.showError(
                        "An ingredient with the name '" +
                                newIngredient.getName() + "' already exists.");
                return;
            }

            server.addIngredient(newIngredient);
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
        String ingredientName = ingredientNameInput.getText().trim();

        if (ingredientName.isEmpty()) {
            mainCtrl.showError("Name cannot be empty.");
            return null;
        }

        try {
            double protein = parseFields(proteinInput.getText());
            double fat = parseFields(fatInput.getText());
            double carbs = parseFields(carbsInput.getText());

            return new Ingredient(ingredientName, protein, fat, carbs);
        } catch (NumberFormatException e) {
            mainCtrl.showError("Please input a valid number for nutrition values.");
        }

        return null;
    }

    private double parseFields(String text) {
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }

    private void updateKcalLabel() {
        try {
            double protein = parseFields(proteinInput.getText());
            double fat = parseFields(fatInput.getText());
            double carbs = parseFields(carbsInput.getText());

            double kcal = (protein * 4) + (carbs * 4) + (fat * 9);

            kcalLabel.setText("Inferred kcal: " + kcal);
        } catch (NumberFormatException e) {
            kcalLabel.setText("Inferred kcal: 0.0");
        }
    }

    private void clearFields() {
        ingredientNameInput.clear();
        proteinInput.clear();
        fatInput.clear();
        carbsInput.clear();
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