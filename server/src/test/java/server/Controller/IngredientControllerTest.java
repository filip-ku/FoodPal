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
        Ingredient ingredient = new Ingredient("Tomato", 10.0, 20.0, 30.0);

        Ingredient savedIngredient = ingredientService.addIngredient(ingredient);

        assertNotNull(savedIngredient.getId());
        assertEquals("Tomato", savedIngredient.getName());
        assertTrue(ingredientRepo.calledMethods.contains("save"));
        assertEquals(1, ingredientRepo.ingredients.size());
    }

    @Test
    void addIngredientNullName() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient ingredient = new Ingredient();

        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> ingredientService.addIngredient(ingredient)
        );

        assertEquals(400, e.getStatusCode().value());
        assertEquals("Ingredient name cannot be null or empty", e.getReason());
        // repo save method should not have been run, as the ingredientService should catch that the
        // name of the ingredient is invalid.
        assertFalse(ingredientRepo.calledMethods.contains("save"));
        // save and findById
        assertEquals(0, ingredientRepo.calledMethods.size());
    }

    @Test
    void getIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        Ingredient ingredient = new Ingredient("Tomato", 10.0, 20.0, 30.0);
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

        Ingredient ingredient1 = ingredientRepo.save(new Ingredient("Tomato", 10.0, 20.0, 30.0));
        Ingredient ingredient2 = ingredientRepo.save(new Ingredient("Cheese", 15.0, 25.0, 35.0));

        ingredients = ingredientService.getAllIngredients();

        assertEquals(2, ingredients.size());
        assertTrue(ingredients.contains(ingredient1));
        assertTrue(ingredients.contains(ingredient2));
        assertEquals("findAll", ingredientRepo.calledMethods.get(3));
    }

    @Test
    void removeIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());
        Ingredient ingredient = new Ingredient("Tomato", 10.0, 20.0, 30.0);
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

        ingredientRepo.save(new Ingredient("Tomato", 10.0, 20.0, 30.0));
        ingredientRepo.save(new Ingredient("Cheese", 15.0, 25.0, 35.0));

        count = ingredientService.countIngredients();
        assertEquals(2, count);
        // Should have called count twice
        assertEquals("count", ingredientRepo.calledMethods.get(3));
    }

    @Test
    void updateIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient originalIngredient = new Ingredient("Tomato", 10.0, 20.0, 30.0);
        Ingredient savedIngredient = ingredientRepo.save(originalIngredient);

        Ingredient updatedIngredient = new Ingredient("Red Tomato", 55.0, null, 70.0);
        Ingredient result = ingredientService.updateIngredient(savedIngredient.getId(), updatedIngredient);

        assertNotNull(result.getId());
        assertEquals("Red Tomato", result.getName());
        assertEquals(55.0, result.getProteinPer100g());
        assertNull(result.getFatPer100g());
        assertEquals(70.0, result.getCarbsPer100g());

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
                () -> ingredientService.updateIngredient(99L, new Ingredient("New Name", 34.6, 23.6, 21.7))
        );

        assertEquals(404, e.getStatusCode().value());
        assertTrue(ingredientRepo.calledMethods.contains("findById"));
        assertFalse(ingredientRepo.calledMethods.contains("save"));
    }

    @Test
    void updateIngredientNullName() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient originalIngredient = new Ingredient("Tomato", 10.0, 20.0, 30.0);
        Ingredient savedIngredient = ingredientRepo.save(originalIngredient);

        Ingredient updatedIngredient = new Ingredient();
        updatedIngredient.setFatPer100g(40.2);

        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> ingredientService.updateIngredient(savedIngredient.getId(), updatedIngredient)
        );

        assertTrue(ingredientRepo.calledMethods.contains("findById"));
        assertEquals(400, e.getStatusCode().value());
        assertEquals("New ingredient name cannot be null or empty", e.getReason());
        // repo save method should not have been run, as the ingredientService should catch that the
        // name of the ingredient is invalid.
        assertNotEquals("save", ingredientRepo.calledMethods.get(1));
        // save and findById
        assertEquals(2, ingredientRepo.calledMethods.size());
    }


    @Test
    void deleteAllIngredients() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        ingredientRepo.save(new Ingredient("Tomato", 10.0, 20.0, 30.0));
        ingredientRepo.save(new Ingredient("Cheese", 15.0, 25.0, 35.0));
        assertEquals(2, ingredientRepo.ingredients.size());

        ingredientService.deleteAllIngredients();

        assertEquals(0, ingredientRepo.ingredients.size());
        assertTrue(ingredientRepo.calledMethods.contains("deleteAll"));
    }

}