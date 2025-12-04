package client.scenes;

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

    public void initialize(Stage primaryStage,
                           Pair<RecipeOverviewCtrl, Parent> overview,
                           Pair<AddRecipeCtrl, Parent> add,
                           Pair<AddIngredientCtrl, Parent> addIngredient,
                           Pair<IngredientsOverviewCtrl, Parent> ingredientsOverview) {
        this.primaryStage = primaryStage;
        this.recipeOverviewCtrl = overview.getKey();
        this.recipeOverview = new Scene(overview.getValue());

        this.addRecipeCtrl = add.getKey();
        this.addRecipe = new Scene(add.getValue());

        this.addIngredientCtrl = addIngredient.getKey();
        this.addIngredient = new Scene(addIngredient.getValue());

        this.ingredientsOverviewCtrl = ingredientsOverview.getKey();
        this.ingredientsOverview = new Scene(ingredientsOverview.getValue());

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
}