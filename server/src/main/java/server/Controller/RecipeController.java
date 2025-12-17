package server.Controller;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.Service.RecipeService;

import java.util.List;

/**
 * REST controller for managing {@link Recipe} entities.
 *
 * <p>The base URL is {@code /api/recipe}. All endpoints are
 * intentionally minimal – they delegate to {@link RecipeService}
 * and return standard HTTP status codes.</p>
 */
@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeService recipeService;

    /**
     * Creates a new controller instance with the required service.
     *
     * @param recipeService injected {@link RecipeService}
     */
    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Adds a new recipe to the system.
     *
     * @param recipe recipe payload (JSON)
     * @return {@code 201 Created} with the persisted entity
     */
    @PostMapping("")
    public ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe) {
        Recipe saved = recipeService.addRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Retrieves all recipes.
     *
     * @return {@code 200 OK} with a list of recipes
     */
    @GetMapping("")
    public ResponseEntity<List<Recipe>> getAllRecipes(){
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    /**
     * Retrieves a single recipe by its identifier.
     *
     * @param id recipe ID
     * @return {@code 200 OK} with the requested recipe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        return ResponseEntity.ok(recipeService.getRecipe(id));
    }

    /**
     * Deletes a recipe.
     *
     * @param id recipe ID to delete
     * @return {@code 204 No Content} on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        recipeService.removeRecipe(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * Updates an existing recipe.
     *
     * @param recipe recipe payload containing updated fields
     * @return {@code 200 OK} with the updated recipe
     */
    @PostMapping("/edit")
    public ResponseEntity<Recipe> updateRecipe(@RequestBody Recipe recipe) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipe));
    }

    /**
     * Adds a new ingredient entry to an existing recipe.
     *
     * @param recipeIngredient the recipe ingredient payload
     * @param id         the id of the recipe to which the ingredient is added
     * @return {@code 200 OK} with the updated recipe
     */
    @PostMapping("/{id}/ingredient")
    public ResponseEntity<Recipe> addRecipeIngredient
    (@RequestBody RecipeIngredient recipeIngredient,
        @PathVariable Long id) {
        Recipe recipe = recipeService.addIngredientToRecipe(id, recipeIngredient);
        return ResponseEntity.ok(recipe);
    }

    /**
     * Adds a new step to an existing recipe.
     *
     * @param recipeStep the recipe step payload
     * @param id  the id of the recipe to which the step is added
     * @return {@code 200 OK} with the updated recipe
     */
    @PostMapping("/{id}/steps")
    public ResponseEntity<Recipe> addRecipeStep(@RequestBody RecipeStep recipeStep,
                                                @PathVariable Long id) {
        Recipe recipe = recipeService.addStepToRecipe(id, recipeStep);
        return ResponseEntity.ok(recipe);
    }

    /**
     * Retrieves all ingredients belonging to the given recipe.
     *
     * @param id the id of the recipe
     * @return {@code 200 OK} with a list of recipe ingredients
     */
    @GetMapping("/{id}/ingredients")
    public ResponseEntity<List<RecipeIngredient>> getRecipeIngredients(@PathVariable long id) {
        return ResponseEntity.ok(recipeService.getIngredientsForRecipe(id));
    }

    /**
     * Retrieves all steps belonging to the given recipe.
     *
     * @param id the id of the recipe
     * @return {@code 200 OK} with a list of recipe steps
     */
    @GetMapping("/{id}/steps")
    public ResponseEntity<List<RecipeStep>> getRecipeSteps(@PathVariable long id) {
        return ResponseEntity.ok(recipeService.getStepsForRecipe(id));
    }

    /**
     * Removes a recipe ingredient from a recipe.
     *
     * @param id           the id of the recipe
     * @param recipeIngredientId the id of the recipe ingredient to remove
     * @return {@code 200 OK} with the updated recipe
     */
    @DeleteMapping("/{id}/ingredients/{recipeIngredientId}")
    public ResponseEntity<Recipe> deleteRecipeIngredient(@PathVariable long id,
                                                         @PathVariable long recipeIngredientId) {
        Recipe updated = recipeService.removeIngredientFromRecipe(id, recipeIngredientId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Removes a step from a recipe.
     *
     * @param id the id of the recipe
     * @param stepId   the id of the step to remove
     * @return {@code 200 OK} with the updated recipe
     */
    @DeleteMapping("/{id}/steps/{stepId}")
    public ResponseEntity<Recipe> deleteRecipeStep(@PathVariable long id,
                                                   @PathVariable long stepId) {
        Recipe updated = recipeService.removeStepFromRecipe(id, stepId);
        return ResponseEntity.ok(updated);
    }


}
