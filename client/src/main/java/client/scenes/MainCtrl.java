package client.scenes;

import client.MyFXML;
import client.utils.ConfigUtils;
import commons.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainCtrl {

    private Stage primaryStage;
    private MyFXML fxml;

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

    private AddRecipeStepCtrl addRecipeStepCtrl;
    private Scene addRecipeStepScene;

    /**
     * Gets the current ResourceBundle based on the saved UI language preference.
     *
     * @return ResourceBundle for the current language
     */
    private ResourceBundle getCurrentResourceBundle() {
        String languageCode = ConfigUtils.getUILanguage();
        Locale locale = new Locale(languageCode);
        return ResourceBundle.getBundle("i18n.messages", locale);
    }

    /**
     * Initializes the application's primary stage and loads all scenes.
     *
     * @param primaryStage           the main application window
     * @param fxml                   MyFXML instance for loading scenes
     * @param overview               Recipe overview pair
     * @param add                    Add-recipe pair
     * @param addIngredient          Add-ingredient pair
     * @param ingredientsOverview    Ingredients overview pair
     * @param chooseRecipeIngredient Choose-recipe-ingredient pair
     * @param addRecipeIngredient    AddRecipeIngredient pair
     * @param addRecipeStep          AddRecipeStep pair
     */
    public void initialize(Stage primaryStage,
                           MyFXML fxml,
                           Pair<RecipeOverviewCtrl, Parent> overview,
                           Pair<AddRecipeCtrl, Parent> add,
                           Pair<AddIngredientCtrl, Parent> addIngredient,
                           Pair<IngredientsOverviewCtrl, Parent> ingredientsOverview,
                           Pair<ChooseRecipeIngredientCtrl, Parent> chooseRecipeIngredient,
                           Pair<AddRecipeIngredientCtrl, Parent> addRecipeIngredient,
                           Pair<AddRecipeStepCtrl, Parent> addRecipeStep) {
        this.primaryStage = primaryStage;
        this.fxml = fxml;
        this.recipeOverviewCtrl = overview.getKey();
        this.recipeOverview = new Scene(overview.getValue());

        this.addRecipeCtrl = add.getKey();
        this.addRecipe = new Scene(add.getValue());

        this.addIngredientCtrl = addIngredient.getKey();
        this.addIngredient = new Scene(addIngredient.getValue());

        this.ingredientsOverviewCtrl = ingredientsOverview.getKey();
        this.ingredientsOverview = new Scene(ingredientsOverview.getValue());

        this.chooseRecipeIngredientCtrl = chooseRecipeIngredient.getKey();
        this.chooseRecipeIngredientScene = new Scene(chooseRecipeIngredient.getValue());

        this.addRecipeIngredientCtrl = addRecipeIngredient.getKey();
        this.addRecipeIngredientScene = new Scene(addRecipeIngredient.getValue());

        this.addRecipeStepCtrl = addRecipeStep.getKey();
        this.addRecipeStepScene = new Scene(addRecipeStep.getValue());

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
        if (primaryStage != null) {
            ResourceBundle resources = getCurrentResourceBundle();
            primaryStage.setTitle(resources.getString("app.title"));
            primaryStage.setScene(recipeOverview);
        }
        if (recipeOverviewCtrl != null) {
            recipeOverviewCtrl.refresh();
        }

    }

    /**
     * Reloads the recipe overview scene with the current language settings.
     * This is used when the UI language is changed at runtime.
     */
    public void reloadRecipeOverview() {
        // Remember the currently selected recipe
        Recipe selectedRecipe = null;
        if (recipeOverviewCtrl != null) {
            selectedRecipe = recipeOverviewCtrl.getSelectedRecipe();
        }

        // Reload the scene with new language bundle
        var overview = fxml
                .load(RecipeOverviewCtrl.class, "client", "scenes", "RecipeOverview.fxml");
        this.recipeOverviewCtrl = overview.getKey();
        this.recipeOverview = new Scene(overview.getValue());

        // Show the reloaded scene
        showRecipeOverview();

        // Restore selection if there was one
        if (selectedRecipe != null) {
            recipeOverviewCtrl.selectRecipe(selectedRecipe);
        }
    }

    /**
     * Displays the "Add Recipe" screen.
     * Reloads the scene to pick up the current language.
     */
    public void showAddRecipe() {
        // Reload scene with current language
        var add = fxml.load(AddRecipeCtrl.class, "client", "scenes", "AddRecipe.fxml");
        this.addRecipeCtrl = add.getKey();
        this.addRecipe = new Scene(add.getValue());

        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.addRecipe"));
        primaryStage.setScene(addRecipe);
        addRecipe.setOnKeyPressed(e -> addRecipeCtrl.keyPressed(e));
    }

    /**
     * Displays the "Add Ingredient" screen.
     * Reloads the scene to pick up the current language.
     */
    public void showAddIngredient() {
        // Reload scene with current language
        var addIng = fxml.load(AddIngredientCtrl.class, "client", "scenes", "AddIngredient.fxml");
        this.addIngredientCtrl = addIng.getKey();
        this.addIngredient = new Scene(addIng.getValue());

        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.addIngredient"));
        primaryStage.setScene(addIngredient);
        addIngredient.setOnKeyPressed(e -> addIngredientCtrl.keyPressed(e));
    }

    /**
     * Displays the ingredients overview screen.
     * Reloads the scene to pick up the current language.
     */
    public void showIngredientsOverview() {
        // Reload scene with current language
        var ingOverview = fxml.load(IngredientsOverviewCtrl.class,
                "client", "scenes", "IngredientOverview.fxml");
        this.ingredientsOverviewCtrl = ingOverview.getKey();
        this.ingredientsOverview = new Scene(ingOverview.getValue());

        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.ingredients"));
        primaryStage.setScene(ingredientsOverview);
        ingredientsOverviewCtrl.refresh();
    }

    /**
     * Getter for the AddIngredientCtrl
     *
     * @return AddIngredientCtrl
     */
    public AddIngredientCtrl getAddIngredientCtrl() {
        return addIngredientCtrl;
    }

    /**
     * Logic to have a pop-up error message
     *
     * @param msg Contents of the error message
     */
    public void showError(String msg) {
        ResourceBundle resources = getCurrentResourceBundle();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resources.getString("dialog.error.title"));
        alert.setHeaderText(resources.getString("dialog.error.header"));
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Shows a pop-up error message that contains the exception message
     *
     * @param e exception error's message
     */
    public void showExceptionErrorPopUp(Exception e) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    /**
     * Displays the "Choose Ingredient" screen for the given recipe.
     * Reloads the scene to pick up the current language.
     *
     * @param recipe the recipe for which an ingredient will be chosen
     */
    public void showChooseRecipeIngredient(Recipe recipe) {
        // Reload scene with current language
        var chooseIng = fxml.load(ChooseRecipeIngredientCtrl.class,
                "client", "scenes", "ChooseRecipeIngredient.fxml");
        this.chooseRecipeIngredientCtrl = chooseIng.getKey();
        this.chooseRecipeIngredientScene = new Scene(chooseIng.getValue());

        chooseRecipeIngredientCtrl.setRecipe(recipe);
        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.chooseIngredient"));
        primaryStage.setScene(chooseRecipeIngredientScene);
    }

    /**
     * Opens the AddRecipeIngredient screen in ADD mode.
     * Reloads the scene to pick up the current language.
     *
     * @param recipe     parent recipe
     * @param ingredient chosen global ingredient
     */
    public void showAddRecipeIngredientForAdd(Recipe recipe, Ingredient ingredient) {
        // Reload scene with current language
        var addRecIng = fxml.load(AddRecipeIngredientCtrl.class,
                "client", "scenes", "AddRecipeIngredient.fxml");
        this.addRecipeIngredientCtrl = addRecIng.getKey();
        this.addRecipeIngredientScene = new Scene(addRecIng.getValue());

        addRecipeIngredientCtrl.setContextForAdd(recipe, ingredient);
        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.addRecipeIngredient"));
        primaryStage.setScene(addRecipeIngredientScene);
    }

    /**
     * Opens the AddRecipeIngredient screen in EDIT mode.
     * Reloads the scene to pick up the current language.
     *
     * @param recipe     parent recipe
     * @param ri         existing recipe-ingredient row to edit
     * @param ingredient global ingredient referenced by the row
     */
    public void showAddRecipeIngredientForEdit(Recipe recipe,
                                               RecipeIngredient ri,
                                               Ingredient ingredient) {
        // Reload scene with current language
        var addRecIng = fxml.load(AddRecipeIngredientCtrl.class,
                "client", "scenes", "AddRecipeIngredient.fxml");
        this.addRecipeIngredientCtrl = addRecIng.getKey();
        this.addRecipeIngredientScene = new Scene(addRecIng.getValue());

        addRecipeIngredientCtrl.setContextForEdit(recipe, ri, ingredient);
        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.editRecipeIngredient"));
        primaryStage.setScene(addRecipeIngredientScene);
    }

    /**
     * Shows the "Add Recipe Step" screen for a given recipe.
     * Reloads the scene to pick up the current language.
     *
     * @param recipe the recipe to which a new step will be added
     */
    public void showAddRecipeStep(Recipe recipe) {
        // Reload scene with current language
        var addStep = fxml.load(AddRecipeStepCtrl.class, "client", "scenes", "AddRecipeStep.fxml");
        this.addRecipeStepCtrl = addStep.getKey();
        this.addRecipeStepScene = new Scene(addStep.getValue());

        addRecipeStepCtrl.setRecipe(recipe);
        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.addStep"));
        primaryStage.setScene(addRecipeStepScene);
    }

    /**
     * Shows the "Edit Recipe Step" screen for a given recipe + existing step.
     * Prefills the AddRecipeStep screen with the selected step data.
     * Reloads the scene to pick up the current language.
     *
     * @param recipe the parent recipe
     * @param step   the step to edit
     */
    public void showEditRecipeStep(Recipe recipe, RecipeStep step) {
        // Reload scene with current language
        var addStep = fxml.load(AddRecipeStepCtrl.class, "client", "scenes", "AddRecipeStep.fxml");
        this.addRecipeStepCtrl = addStep.getKey();
        this.addRecipeStepScene = new Scene(addStep.getValue());

        addRecipeStepCtrl.setContextForEdit(recipe, step);
        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.editStep"));
        primaryStage.setScene(addRecipeStepScene);
    }

    /**
     * Displays the edit ingredient screen for the given recipe ingredient.
     * Reloads the scene to pick up the current language.
     *
     * @param recipe           the parent recipe
     * @param recipeIngredient the ingredient to edit
     */
    public void showEditRecipeIngredient(Recipe recipe, RecipeIngredient recipeIngredient) {
        // Reload scene with current language
        var addRecIng = fxml.load(AddRecipeIngredientCtrl.class,
                "client", "scenes", "AddRecipeIngredient.fxml");
        this.addRecipeIngredientCtrl = addRecIng.getKey();
        this.addRecipeIngredientScene = new Scene(addRecIng.getValue());

        addRecipeIngredientCtrl.setContextForEdit(
                recipe,
                recipeIngredient,
                recipeIngredient.getIngredient()
        );
        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.editRecipeIngredient"));
        primaryStage.setScene(addRecipeIngredientScene);
    }

    /**
     * Shows addIngredient scene in edit mode
     * Reloads the scene to pick up the current language.
     *
     * @param ingredient the ingredient to edit
     */
    public void showEditIngredient(Ingredient ingredient) {
        // Reload scene with current language
        var addIng = fxml.load(AddIngredientCtrl.class, "client", "scenes", "AddIngredient.fxml");
        this.addIngredientCtrl = addIng.getKey();
        this.addIngredient = new Scene(addIng.getValue());

        ResourceBundle resources = getCurrentResourceBundle();
        primaryStage.setTitle(resources.getString("window.title.editIngredient"));
        addIngredientCtrl.setIngredientToEdit(ingredient);
        primaryStage.setScene(addIngredient);
    }

    /**
     * Shows the Recipe Overview screen with a pre-filled search query.
     * The search is automatically executed to filter recipes.
     * AI generated javadoc
     * @param searchQuery the text to search for (typically an ingredient name)
     */
    public void showRecipeOverviewWithSearch(String searchQuery) {
        showRecipeOverview();
        recipeOverviewCtrl.setSearchQuery(searchQuery);
    }
}