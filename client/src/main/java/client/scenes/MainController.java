package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainController {
    private Stage primaryStage;

    private RecipeOverviewController recipeOverviewController;
    private Scene recipeOverview;

    private AddRecipeController addRecipeController;
    private Scene addRecipe;

    public void initialize(Stage primaryStage, Pair<RecipeOverviewController, Parent> overview,
                                               Pair<AddRecipeController, Parent> add) {
        this.primaryStage = primaryStage;
        this.recipeOverviewController = overview.getKey();
        this.recipeOverview = new Scene(overview.getValue());

        this.addRecipeController = add.getKey();
        this.addRecipe = new Scene(add.getValue());

        showRecipeOverview();
        primaryStage.show();
    }

    public void showRecipeOverview() {
        primaryStage.setTitle("Recipes: Overview");
        primaryStage.setScene(recipeOverview);
        recipeOverviewController.refresh();
    }

    public void showAddRecipe() {
        primaryStage.setTitle("Recipes: Adding Recipe");
        primaryStage.setScene(addRecipe);
        addRecipe.setOnKeyPressed(e -> addRecipeController.keyPressed(e));
    }
}
