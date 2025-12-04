package server.Controller;

import commons.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import server.Service.IngredientService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class which is used to test {@link IngredientController}
 */
public class IngredientControllerTest {

    private TestIngredientRepository ingredientRepo;

    private IngredientService ingredientService;

    @BeforeEach
    public void setUp() {
        ingredientRepo = new TestIngredientRepository();
        ingredientService = new IngredientService(ingredientRepo);
    }

    @Test
    void addIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        Ingredient ingredient = new Ingredient("Tomato");

        Ingredient savedIngredient = ingredientRepo.save(ingredient);

        assertNotNull(savedIngredient.getId());
        assertEquals("Tomato", savedIngredient.getName());
        assertTrue(ingredientRepo.calledMethods.contains("save"));
        assertEquals(1, ingredientRepo.ingredients.size());
    }

    @Test
    void addIngredientEmptyName() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        Ingredient ingredient = new Ingredient("");

        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> ingredientService.addIngredient(ingredient)
        );

        assertEquals(400, e.getStatusCode().value());
        assertEquals("Ingredient name cannot be null or empty", e.getReason());

        // repo save method should not have been run, as the ingredientService should catch the empty name.
        assertTrue(ingredientRepo.calledMethods.isEmpty());
    }

    @Test
    void getIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        Ingredient ingredient = new Ingredient("Tomato");
        ingredientRepo.save(ingredient);
        assertTrue(ingredientRepo.calledMethods.contains("save"));

        Ingredient result = ingredientService.getIngredient(ingredient.getId());
        assertEquals("Tomato", result.getName());
    }

    @Test
    void getIngredientNotFound() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> ingredientService.getIngredient(99L)
        );

        assertEquals(404, e.getStatusCode().value());
        assertTrue(ingredientRepo.calledMethods.contains("findById"));
    }

    @Test
    void getAllIngredients() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        assertEquals(0, ingredients.size());
        assertEquals("findAll", ingredientRepo.calledMethods.get(0));

        Ingredient ingredient1 = ingredientRepo.save(new Ingredient("Tomato"));
        Ingredient ingredient2 = ingredientRepo.save(new Ingredient("Cheese"));

        ingredients = ingredientService.getAllIngredients();

        assertEquals(2, ingredients.size());
        assertTrue(ingredients.contains(ingredient1));
        assertTrue(ingredients.contains(ingredient2));
        assertEquals("findAll", ingredientRepo.calledMethods.get(3));
    }

    @Test
    void removeIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        Ingredient ingredient = new Ingredient("Tomato");
        ingredientRepo.save(ingredient);
        assertEquals(1, ingredientRepo.ingredients.size());

        ingredientService.removeIngredient(ingredient.getId());

        assertEquals(0, ingredientRepo.ingredients.size());
        assertTrue(ingredientRepo.calledMethods.contains("deleteById"));
    }

    @Test
    void removeIngredientNotFound() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> ingredientService.removeIngredient(123L)
        );

        assertEquals(404, ex.getStatusCode().value());

        // repo removeById method should not have been run, as the ingredientService should catch that there is
        // no ingredient with the provided id.
        assertFalse(ingredientRepo.calledMethods.contains("deleteById"));
    }

    @Test
    void countIngredients() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        long count = ingredientService.countIngredients();
        assertEquals(0, count);
        assertEquals("count", ingredientRepo.calledMethods.get(0));

        ingredientRepo.save(new Ingredient("Tomato"));
        ingredientRepo.save(new Ingredient("Cheese"));

        count = ingredientService.countIngredients();
        assertEquals(2, count);
        // Should have called count twice
        assertEquals("count", ingredientRepo.calledMethods.get(3));
    }

    @Test
    void updateIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient originalIngredient = new Ingredient("Tomato");
        Ingredient savedIngredient = ingredientRepo.save(originalIngredient);

        Ingredient updatedIngredient = new Ingredient("Red Tomato");
        Ingredient result = ingredientService.updateIngredient(savedIngredient.getId(), updatedIngredient);

        assertNotNull(result.getId());
        assertEquals("Red Tomato", result.getName());
        assertTrue(ingredientRepo.calledMethods.contains("findById"));
        assertTrue(ingredientRepo.calledMethods.contains("save"));
        // save, findById and save
        assertEquals(3, ingredientRepo.calledMethods.size());
    }

    @Test
    void updateIngredientNotFound() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> ingredientService.updateIngredient(99L, new Ingredient("New Name"))
        );

        assertEquals(404, e.getStatusCode().value());
        assertTrue(ingredientRepo.calledMethods.contains("findById"));
        assertFalse(ingredientRepo.calledMethods.contains("save"));
    }

    @Test
    void deleteAllIngredients() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        ingredientRepo.save(new Ingredient("Tomato"));
        ingredientRepo.save(new Ingredient("Cheese"));
        assertEquals(2, ingredientRepo.ingredients.size());

        ingredientService.deleteAllIngredients();

        assertEquals(0, ingredientRepo.ingredients.size());
        assertTrue(ingredientRepo.calledMethods.contains("deleteAll"));
    }

}