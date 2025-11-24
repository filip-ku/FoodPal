package server.Controller;

import commons.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.Service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    public final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("")
    public ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe) {
        Recipe saved = recipeService.addRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("")
    public ResponseEntity<List<Recipe>> getAllRecipes(){
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        return ResponseEntity.ok(recipeService.getRecipe(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        recipeService.removeRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
