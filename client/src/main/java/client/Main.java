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
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import com.google.inject.Injector;

import client.utils.ServerUtils;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX client application.
 *
 * <p>All heavy lifting (e.g., UI layout, business logic) is delegated to
 * controllers; {@code Main} merely glues everything together.</p>
 */
public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Launches the JavaFX application.
     *
     * @param args args command‑line arguments (ignored)
     * @throws URISyntaxException if a resource URI is malformed
     * @throws IOException        if an FXML file cannot be read
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * Initializes the primary stage and wires together the UI.
     *
     * @param primaryStage the primary stage provided by JavaFX
     * @throws Exception if an unexpected error occurs during initialization
     */
    @Override
	public void start(Stage primaryStage) throws Exception {

        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client," +
                    "but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        var recipeOverview = FXML.load(RecipeOverviewCtrl.class, "client","scenes",
                "RecipeOverview.fxml");
        var addRecipe = FXML.load(AddRecipeCtrl.class, "client", "scenes", "AddRecipe.fxml");
        var addIngredient = FXML.load(AddIngredientCtrl.class, "client",
                "scenes", "AddIngredient.fxml");
        var ingredientOverview = FXML.load(IngredientsOverviewCtrl.class, "client","scenes",
                "IngredientOverview.fxml");
        var chooseRecipeIngredient = FXML.load(ChooseRecipeIngredientCtrl.class,
                "client","scenes","ChooseRecipeIngredient.fxml");
        var addRecipeIngredient = FXML.load(AddRecipeIngredientCtrl.class,
                "client","scenes","AddRecipeIngredient.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage,
                recipeOverview,
                addRecipe,
                addIngredient,
                ingredientOverview,
                chooseRecipeIngredient,
                addRecipeIngredient);
    }
}