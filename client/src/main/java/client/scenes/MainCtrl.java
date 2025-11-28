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

    public void initialize(Stage primaryStage, Pair<RecipeOverviewCtrl, Parent> overview,
                           Pair<AddRecipeCtrl, Parent> add, Pair<AddIngredientCtrl, Parent> addIngredient) {
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

    public RecipeOverviewCtrl getRecipeOverviewCtrl() {
        return recipeOverviewCtrl;
    }

    public void showRecipeOverview() {
        primaryStage.setTitle("FoodPal");
        primaryStage.setScene(recipeOverview);
        recipeOverviewCtrl.refresh();
    }

    public void showAddRecipe() {
        primaryStage.setTitle("FoodPal: Adding Recipe");
        primaryStage.setScene(addRecipe);
        addRecipe.setOnKeyPressed(e -> addRecipeCtrl.keyPressed(e));
    }

    public void showAddIngredient() {
        primaryStage.setTitle("FoodPal: Adding an ingredient");
        primaryStage.setScene(addIngredient);
        addIngredient.setOnKeyPressed(e -> addIngredientCtrl.keyPressed(e));
    }

    public AddIngredientCtrl getAddIngredientCtrl() {
        return addIngredientCtrl;
    }
}