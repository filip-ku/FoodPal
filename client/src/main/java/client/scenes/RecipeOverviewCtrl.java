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

    @Inject
    public RecipeOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void addIngredientToTable(Ingredient ingredient, int quantity, String units, String notes) {
        RecipeIngredient newIngredient = new RecipeIngredient(ingredient, quantity, units, notes);
        tableIngredients.getItems().add(newIngredient);
    }

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

    public void selectRecipe(Recipe recipe) {
        if (recipe == null) return;
        tableRecipes.getSelectionModel().select(recipe);
    }

    public void refresh() {
        var recipes = server.getRecipes();
        data = FXCollections.observableList(recipes);
        tableRecipes.setItems(data);
    }

    /**
     * Logic for when the user wants to edit the recipe name
     */
    public void editNameClicked() {
        if (!editingName) {
            toggleEditMode(true);
            recipeEditBox.setText(recipeName.getText());
        } else {
            String newName = recipeEditBox.getText();

            if (newName == null || newName.trim().isEmpty()) {
                showError("Name cannot be empty.");
                return;
            }
            recipeName.setText(newName);

            Recipe selected = tableRecipes.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setTitle(newName);
            }

            // TODO
            // Needs a way to update the database through the server

            editingName = false;
            toggleEditMode(false);
        }
    }

    public void toggleEditMode(boolean editing) {
        recipeEditBox.setVisible(editing);
        recipeEditBox.setDisable(!editing);

        recipeName.setVisible(!editing);
        recipeEditButton.setText(editing ? "Save" : "Edit");
    }

    /**
     * Logic for deleting a recipe
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

    @FXML
    private void editSteps() {
        // TODO: implement later
        showError("Editing steps is not implemented yet.");
    }

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