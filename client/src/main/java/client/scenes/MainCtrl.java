package client.scenes;

import commons.Recipe;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
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

    private IngredientsOverviewCtrl ingredientsOverviewCtrl;
    private Scene ingredientsOverview;

    private ChooseRecipeIngredientCtrl chooseRecipeIngredientCtrl;
    private Scene chooseRecipeIngredientScene;

    /**
     * Initializes the application’s primary stage and loads all scenes.
     *
     * @param primaryStage the main application window
     * @param overview Recipe overview controller + root
     * @param add Add-recipe controller + root
     * @param addIngredient Add-ingredient controller + root
     * @param ingredientsOverview Ingredients overview controller + root
     * @param chooseRecipeIngredient Choose-recipe-ingredient controller + root
     */
    public void initialize(Stage primaryStage,
                           Pair<RecipeOverviewCtrl, Parent> overview,
                           Pair<AddRecipeCtrl, Parent> add,
                           Pair<AddIngredientCtrl, Parent> addIngredient,
                           Pair<IngredientsOverviewCtrl, Parent> ingredientsOverview,
                           Pair<ChooseRecipeIngredientCtrl, Parent> chooseRecipeIngredient) {
        this.primaryStage = primaryStage;
        this.recipeOverviewCtrl = overview.getKey();
        this.recipeOverview = new Scene(overview.getValue());

        this.addRecipeCtrl = add.getKey();
        this.addRecipe = new Scene(add.getValue());

        this.addIngredientCtrl = addIngredient.getKey();
        this.addIngredient = new Scene(addIngredient.getValue());

        this.ingredientsOverviewCtrl = ingredientsOverview.getKey();
        this.ingredientsOverview = new Scene(ingredientsOverview.getValue());

        this.chooseRecipeIngredientCtrl  = chooseRecipeIngredient.getKey();
        this.chooseRecipeIngredientScene = new Scene(chooseRecipeIngredient.getValue());

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
        primaryStage.setTitle("FoodPal: Adding Ingredient");
        primaryStage.setScene(addIngredient);
        addIngredient.setOnKeyPressed(e -> addIngredientCtrl.keyPressed(e));
    }

    public void showIngredientsOverview() {
        primaryStage.setTitle("FoodPal: Ingredients");
        primaryStage.setScene(ingredientsOverview);
        ingredientsOverviewCtrl.refresh();
    }

    public AddIngredientCtrl getAddIngredientCtrl() {
        return addIngredientCtrl;
    }

    /**
     * Logic to have a pop-up error message
     *
     * @param msg Contents of the error message
     */
    public void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void showExceptionErrorPopUp(Exception e) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    /**
     * Displays the “Choose Ingredient” screen for the given recipe.
     *
     * @param recipe the recipe for which an ingredient will be chosen
     */
    public void showChooseRecipeIngredient(Recipe recipe) {
        chooseRecipeIngredientCtrl.setRecipe(recipe);
        primaryStage.setTitle("Choose Ingredient");
        primaryStage.setScene(chooseRecipeIngredientScene);
    }
}