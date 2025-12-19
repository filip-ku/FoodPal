package server.Controller;

import commons.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.Service.IngredientService;
import java.util.List;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientController {

    private final IngredientService ingredientService;
    private static final Logger log = LoggerFactory.getLogger(IngredientController.class);

    /**
     * Constructs a controller with the required link
     * @param ingredientService the service handling ingredient operations
     */
    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * Making new ingredients
     * @param ingredient the ingredient to be made
     * @return the new ingredient with status CREATED
     */
    @PostMapping("")
    public ResponseEntity<Ingredient> addIngredient(@RequestBody Ingredient ingredient){
        Ingredient saved = ingredientService.addIngredient(ingredient);
        log.info("Ingredient created with ID {}",  saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Counts the number of ingredients that are in the system
     * @return count of all ingredients
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countIngredients(){
        log.info("Received request to count ingredients");
        long count = ingredientService.countIngredients();
        return ResponseEntity.ok(count);
    }

    public ResponseEntity<Long> getIngredientUsage(@PathVariable Long id){
        long usage = ingredientService.countUsageOfIngredient(id);
        return ResponseEntity.ok(usage);
    }

    /**
     * Takes all ingredients
     * @return a list of all ingredients
     */
    @GetMapping("")
    public ResponseEntity<List<Ingredient>> getAllIngredients(){
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    /**
     * Gets a single ingredient by the ID
     * @param id the ID of the ingredient
     * @return the ingredient name if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable long id){
        log.info("getting ingredient with id {}", id);
        return ResponseEntity.ok(ingredientService.getIngredient(id));
    }

    /**
     * Deletes an ingredient by the ID
     * @param id the ID of the ingredient
     * @return HTTP 204 No content if successfully deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable long id){
        log.info("deleting ingredient with id {}", id);
        ingredientService.removeIngredient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes all ingredients
     * @return HTTP 204 No content if successfully deleted
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void>  deleteAllIngredients(){
        log.info("Deleting all ingredients");
        ingredientService.deleteAllIngredients();
        return  ResponseEntity.noContent().build();
    }

    /**
     * Updates the ingredient name from an id
     * @param id the id of the ingredient to be updated
     * @param ingredient data that will be updated to
     * @return the changed ingredient
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable long id,
                                                       @RequestBody Ingredient ingredient){
        log.info("updating ingredient {}", id);
        Ingredient updated = ingredientService.updateIngredient(id, ingredient);
        return ResponseEntity.ok(updated);
    }

    /**
     * searches ingredients by name
     * @param query input for search
     * @return list of ingredients containing query
     */
    @GetMapping("/search")
    public ResponseEntity<List<Ingredient>> searchIngredients(@RequestParam String query){
        return ResponseEntity.ok(ingredientService.searchIngredients(query));
    }
}
