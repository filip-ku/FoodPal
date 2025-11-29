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

/**
 * Main UI controller – owns the primary stage and switches between scenes.
 */
public class MainCtrl {

    private Stage primaryStage;

    private RecipeOverviewCtrl recipeOverviewCtrl;
    private Scene recipeOverview;

    private AddRecipeCtrl addRecipeCtrl;
    private Scene addRecipe;

    /**
     * Initializes this controller with the primary stage and the two scenes.
     *
     * @param primaryStage   main application window
     * @param overview       pair of {@link RecipeOverviewCtrl} and its root node
     * @param add            pair of {@link AddRecipeCtrl} and its root node
     */
    public void initialize(Stage primaryStage, Pair<RecipeOverviewCtrl, Parent> overview,
            Pair<AddRecipeCtrl, Parent> add) {
        this.primaryStage = primaryStage;
        this.recipeOverviewCtrl = overview.getKey();
        this.recipeOverview = new Scene(overview.getValue());

        this.addRecipeCtrl = add.getKey();
        this.addRecipe = new Scene(add.getValue());

        showRecipeOverview();
        primaryStage.show();
    }

    /**
     * Displays the recipe‑overview scene and refreshes its contents.
     */
    public void showRecipeOverview() {
        primaryStage.setTitle("FoodPal");
        primaryStage.setScene(recipeOverview);
        recipeOverviewCtrl.refresh();
    }

    /**
     * Switches to the “Add Recipe” scene and forwards key events to its controller.
     */
    public void showAddRecipe() {
        primaryStage.setTitle("FoodPal: Adding Recipe");
        primaryStage.setScene(addRecipe);
        addRecipe.setOnKeyPressed(e -> addRecipeCtrl.keyPressed(e));
    }
}