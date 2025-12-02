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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private RecipeOverviewCtrl recipeOverviewCtrl;
    private Scene recipeOverview;

    private AddRecipeCtrl addRecipeCtrl;
    private Scene addRecipe;

    private AddIngredientCtrl addIngredientCtrl;
    private Scene addIngredient;

    /**
     * Initializes the main controller and sets up all application scenes.
     *
     * @param primaryStage the main application stage
     * @param overview     the recipe overview controller and layout
     * @param add          the add recipe controller and layout
     * @param addIngredient the add ingredient controller and layout
     */
    public void initialize(Stage primaryStage,
                           Pair<RecipeOverviewCtrl, Parent> overview,
                           Pair<AddRecipeCtrl, Parent> add,
                           Pair<AddIngredientCtrl, Parent> addIngredient) {
        this.primaryStage = primaryStage;
        this.recipeOverviewCtrl = overview.getKey();
        this.recipeOverview = new Scene(overview.getValue());

        this.addRecipeCtrl = add.getKey();
        this.addRecipe = new Scene(add.getValue());
        this.addIngredientCtrl = addIngredient.getKey();
        this.addIngredient = new Scene(addIngredient.getValue());

        showRecipeOverview();
        primaryStage.show();
    }

    /**
     * Returns the controller for the recipe overview screen.
     *
     * @return the recipe overview controller
     */
    public RecipeOverviewCtrl getRecipeOverviewCtrl() {
        return recipeOverviewCtrl;
    }

    /**
     * Displays the recipe overview screen.
     */
    public void showRecipeOverview() {
        primaryStage.setTitle("FoodPal");
        primaryStage.setScene(recipeOverview);
        recipeOverviewCtrl.refresh();
    }

    /**
     * Displays the "Add Recipe" screen.
     */
    public void showAddRecipe() {
        primaryStage.setTitle("FoodPal: Adding Recipe");
        primaryStage.setScene(addRecipe);
        addRecipe.setOnKeyPressed(e -> addRecipeCtrl.keyPressed(e));
    }

    /**
     * Displays the "Add Ingredient" screen.
     */
    public void showAddIngredient() {
        primaryStage.setTitle("FoodPal: Adding an ingredient");
        primaryStage.setScene(addIngredient);
        addIngredient.setOnKeyPressed(e -> addIngredientCtrl.keyPressed(e));
    }


    /**
     * Returns the controller for the "Add Ingredient" screen.
     *
     * @return the add ingredient controller
     */
    public AddIngredientCtrl getAddIngredientCtrl() {
        return addIngredientCtrl;
    }
}