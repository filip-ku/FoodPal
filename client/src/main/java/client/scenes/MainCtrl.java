package client.scenes;

import commons.*;
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

    private AddRecipeIngredientCtrl addRecipeIngredientCtrl;
    private Scene addRecipeIngredientScene;

    /**
     * Initializes the application’s primary stage and loads all scenes.
     *
     * @param primaryStage the main application window
     * @param overview Recipe overview pair
     * @param add Add-recipe pair
     * @param addIngredient Add-ingredient pair
     * @param ingredientsOverview Ingredients overview pair
     * @param chooseRecipeIngredient Choose-recipe-ingredient pair
     * @param addRecipeIngredient AddRecipeIngredient pair
     */
    public void initialize(Stage primaryStage,
                           Pair<RecipeOverviewCtrl, Parent> overview,
                           Pair<AddRecipeCtrl, Parent> add,
                           Pair<AddIngredientCtrl, Parent> addIngredient,
                           Pair<IngredientsOverviewCtrl, Parent> ingredientsOverview,
                           Pair<ChooseRecipeIngredientCtrl, Parent> chooseRecipeIngredient,
                           Pair<AddRecipeIngredientCtrl, Parent> addRecipeIngredient) {
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

        this.addRecipeIngredientCtrl = addRecipeIngredient.getKey();
        this.addRecipeIngredientScene = new Scene(addRecipeIngredient.getValue());

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

    /**
     * Opens the AddRecipeIngredient screen in ADD mode.
     * @param recipe     parent recipe
     * @param ingredient chosen global ingredient
     */
    public void showAddRecipeIngredientForAdd(Recipe recipe, Ingredient ingredient) {
        addRecipeIngredientCtrl.setContextForAdd(recipe, ingredient);
        primaryStage.setTitle("FoodPal: Add Ingredient to Recipe");
        primaryStage.setScene(addRecipeIngredientScene);
    }

    /**
     * Opens the AddRecipeIngredient screen in EDIT mode.
     * @param recipe     parent recipe
     * @param ri         existing recipe-ingredient row to edit
     * @param ingredient global ingredient referenced by the row
     */
    public void showAddRecipeIngredientForEdit(Recipe recipe,
                                               RecipeIngredient ri,
                                               Ingredient ingredient) {
        addRecipeIngredientCtrl.setContextForEdit(recipe, ri, ingredient);
        primaryStage.setTitle("FoodPal: Edit Ingredient in Recipe");
        primaryStage.setScene(addRecipeIngredientScene);
    }

}