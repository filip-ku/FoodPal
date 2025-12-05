package server.Controller;

import commons.Recipe;
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
     * copies a recipe
     * @param id the id of the recipe to be cloned
     * @param title optional title for the copied recipe
     * @return copied recipe
     */
    @PostMapping("/{id}/clone")
    public ResponseEntity<Recipe> cloneRecipe(@PathVariable long id,
                                              @RequestParam(value = "title", required = false) String title) {
        Recipe cloned = recipeService.cloneRecipe(id, title);
        return ResponseEntity.status(HttpStatus.CREATED).body(cloned);
    }

    /**
     * creates a printable version of a recipe
     * @param id the id of the recipe to be printed
     * @return a printable text of a recipe
     */
    @GetMapping("/{id}/print")
    public ResponseEntity<String> getRecipePrint(@PathVariable long id) {
        String printable = recipeService.getPrintableRecipe(id);
        return ResponseEntity.ok(printable);
    }
}
