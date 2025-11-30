/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
//import server.RecipeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class RecipeOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<Recipe> data;

    @FXML
    private TableView<Recipe> tableRecipes;
    @FXML
    private TableColumn<Recipe, String> colRecipes;

    @FXML
    private TableView<RecipeIngredient> tableIngredients; //temporarily a string
    @FXML
    private TableColumn<RecipeIngredient, String> colIngredients;

    @FXML
    private TableView<RecipeStep> tablePreparation;
    @FXML
    private TableColumn<RecipeStep, String> colPreparation;

    @FXML
    private Label recipeName;
    @FXML
    private TextField recipeEditBox;
    @FXML
    private Button recipeEditButton;
    private boolean editingName = false;
    @FXML
    private Button editStepsButton;

    /**
     * Constructs a {@code RecipeOverviewCtrl}.
     *
     * <p>Instances are created via Guice injection. The controller receives a
     * reference to {@link ServerUtils} for server communication and a
     * {@link MainCtrl} for navigation.</p>
     *
     * @param server  injected {@link ServerUtils}
     * @param mainCtrl injected {@link MainCtrl}
     */
    @Inject
    public RecipeOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Adds the new ingredient to the table.
     *
     * @param ingredient ingredient to add
     * @param quantity quantity of ingredient
     * @param units units of measurement
     * @param notes optional notes about the ingredient
     */
    public void addIngredientToTable(Ingredient ingredient, int quantity,
                                     String units, String notes) {
        // TODO: change method to use new commons structure
//        RecipeIngredient newIngredient = new RecipeIngredient(ingredient, quantity, units, notes);
//        tableIngredients.getItems().add(newIngredient);
    }

    /**
     * Called by the JavaFX framework after the FXML elements have been injected.
     *
     * <p>Initialises UI bindings and listeners, then shows the default
     * “main menu” view.</p>
     *
     * @param location  location of the FXML file (unused)
     * @param resources resource bundle for internationalisation (unused)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showMainMenu();

        colRecipes.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getTitle()));

        colIngredients.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().toString())
        );

        tableRecipes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        recipeName.setText(newSel.getTitle());

        //                TODO
        //                Need to implement logic for Ingredients and Preparation tables

                        tableIngredients.setVisible(true);
                        tablePreparation.setVisible(true);

                        recipeEditButton.setVisible(true);
                        recipeName.setVisible(true);
                    }
                });
    }

    /**
     * Navigates to the “Add Recipe” screen.
     */
    public void addRecipe() {
        mainCtrl.showAddRecipe();
    }

    /**
     * Opens the AddIngredient scene for the selected recipe
     */
    public void addIngredient() {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No recipe selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a recipe first.");
            alert.showAndWait();
            return;
        }
        mainCtrl.getAddIngredientCtrl().setRecipe(selected);
        mainCtrl.showAddIngredient();
    }

    /**
     * Selects the given recipe in the table.
     *
     * @param recipe the recipe to select
     */
    public void selectRecipe(Recipe recipe) {
        if (recipe == null) return;
        tableRecipes.getSelectionModel().select(recipe);
    }

    /**
     * Refreshes the recipe list from the server and updates the table view.
     */
    public void refresh() {
        var recipes = server.getRecipes();
        data = FXCollections.observableList(recipes);
        tableRecipes.setItems(data);
    }

    /**
     * Handles the Edit/Save button click for a recipe name.
     *
     * <p>When not in edit mode, switches to an editable {@link TextField}.
     * When already editing, validates the input and updates the model.</p>
     */
    public void editNameClicked() {
        if (!editingName) {
            editingName = true;
            recipeEditButton.setText("Save");

            recipeEditBox.setText(recipeName.getText());
            recipeEditBox.setDisable(false);
            recipeEditBox.setVisible(true);

            recipeName.setVisible(false);
        } else {
            String newName = recipeEditBox.getText();

            if (newName == null || newName.trim().isEmpty()) {
                showError("Name cannot be empty.");
                return;
            }
            recipeName.setText(newName);

            editingName = false;
            recipeEditButton.setText("Edit");

            Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setTitle(newName);
            }

            // TODO
            // Needs a way to update the database through the server

            tableRecipes.refresh();

            recipeEditBox.setDisable(true);
            recipeEditBox.setVisible(false);

            recipeName.setVisible(true);
        }
    }


    /**
     * Deletes the currently selected recipe after user confirmation.
     *
     * <p>Shows a confirmation dialog. If confirmed, removes the recipe
     * from the observable list and updates the table view.</p>
     */
    public void deleteRecipe() {
        Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("No recipe selected.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Recipe?");
        confirm.setContentText("You sure you wanna delete this recipe?");

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        // TODO
        // Needs a way to update the database through the server

        showMainMenu();

        data.remove(selected);
    }

    /**
     * Method is not available yet
     */
    @FXML
    private void editSteps() {
        // TODO: implement later
        showError("Editing steps is not implemented yet.");
    }

    /**
     * Deletes the currently selected ingredient after user confirmation.
     *
     * <p>Shows a confirmation dialog. If confirmed, removes the ingredient
     * from the observable list and updates the table view.</p>
     */
    @FXML
    public void deleteIngredient() {
        RecipeIngredient selected = tableIngredients.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an ingredient to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Ingredient?");
        confirm.setContentText("Are you sure you want to remove this ingredient?");

        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        tableIngredients.getItems().remove(selected);

    }

    /**
     * Logic to have a pop-up error message
     *
     * @param msg Contents of the error message
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Makes every component except the label invisible
     * Sets the label text to "Welcome to FoodPal!"
     */
    public void showMainMenu() {
        recipeEditButton.setVisible(false);
        recipeName.setText("Welcome to FoodPal!");
        tableIngredients.setVisible(false);
        tablePreparation.setVisible(false);
    }
}