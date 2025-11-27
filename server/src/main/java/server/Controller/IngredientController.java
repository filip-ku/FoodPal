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

    public final IngredientService ingredientService;
    private static final Logger log = LoggerFactory.getLogger(IngredientController.class);

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping("")
    public ResponseEntity<Ingredient> addIngredient(@RequestBody Ingredient ingredient){
        Ingredient saved = ingredientService.addIngredient(ingredient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countIngredients(){
        log.info("Received request to count ingredients");
        long count = ingredientService.countIngredients();
        return ResponseEntity.ok(count);
    }

    @GetMapping("")
    public ResponseEntity<List<Ingredient>> getAllIngredients(){
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredient(@PathVariable long id){
        log.info("getting ingredient with id {}", id);
        return ResponseEntity.ok(ingredientService.getIngredient(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable long id){
        log.info("deleting ingredient with id {}", id);
        ingredientService.removeIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
