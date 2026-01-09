package server.Controller;

import commons.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.Service.IngredientService;
import server.Repository.RecipeRepository;
import server.ws.WebSocketService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link IngredientController}.
 * Uses {@link TestIngredientRepository} to test controller behavior
 * with actual service implementation.
 */
class IngredientControllerTest {

    private TestIngredientRepository ingredientRepo;
    private IngredientService ingredientService;
    private IngredientController ingredientController;

    private RecipeRepository recipeRepo;
    private WebSocketService webSocketService;

    @BeforeEach
    void setUp() {
        ingredientRepo = new TestIngredientRepository();
        recipeRepo = mock(RecipeRepository.class);
        webSocketService = mock(WebSocketService.class);

        ingredientService = new IngredientService(ingredientRepo, recipeRepo,
                webSocketService);
        ingredientController = new IngredientController(ingredientService);
    }

    @Test
    void testAddIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Tomato");

        ResponseEntity<Ingredient> response = ingredientController.addIngredient(ingredient);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Tomato", response.getBody().getName());
        assertTrue(ingredientRepo.calledMethods.contains("save"));
        assertEquals(1, ingredientRepo.ingredients.size());

        verify(webSocketService)
                .publishIngredientListChanged(response.getBody().getId());
    }

    @Test
    void testAddMultipleIngredients() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Onion");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Garlic");

        ingredientController.addIngredient(ingredient1);
        ingredientController.addIngredient(ingredient2);

        assertEquals(2, ingredientRepo.ingredients.size());
        assertEquals(2, ingredientRepo.calledMethods.stream()
                .filter(m -> m.equals("save")).count());
    }

    @Test
    void testCountIngredients() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        ResponseEntity<Long> response = ingredientController.countIngredients();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0L, response.getBody());
        assertTrue(ingredientRepo.calledMethods.contains("count"));

        ingredientRepo.save(new Ingredient());
        ingredientRepo.save(new Ingredient());

        response = ingredientController.countIngredients();

        assertEquals(2L, response.getBody());
    }

    @Test
    void testGetAllIngredientsEmpty() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        ResponseEntity<List<Ingredient>> response = ingredientController.getAllIngredients();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().size());
        assertTrue(ingredientRepo.calledMethods.contains("findAll"));
    }

    @Test
    void testGetAllIngredientsWithData() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient ing1 = new Ingredient();
        ing1.setName("Pepper");
        Ingredient ing2 = new Ingredient();
        ing2.setName("Salt");

        ingredientRepo.save(ing1);
        ingredientRepo.save(ing2);

        ResponseEntity<List<Ingredient>> response = ingredientController.getAllIngredients();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertTrue(ingredientRepo.calledMethods.contains("findAll"));
    }

    @Test
    void testGetIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Carrot");
        ingredientRepo.save(ingredient);
        assertTrue(ingredientRepo.calledMethods.contains("save"));

        ResponseEntity<Ingredient> response = ingredientController
                .getIngredient(ingredient.getId());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Carrot", response.getBody().getName());
        assertTrue(ingredientRepo.calledMethods.contains("findById"));
    }

    @Test
    void testDeleteIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Cucumber");
        ingredientRepo.save(ingredient);
        Long id = ingredient.getId();

        assertEquals(1, ingredientRepo.ingredients.size());

        ResponseEntity<Void> response = ingredientController.deleteIngredient(id);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        assertEquals(0, ingredientRepo.ingredients.size());
        assertTrue(ingredientRepo.calledMethods.contains("deleteById"));

        verify(webSocketService)
                .publishIngredientListChanged(id);
    }

    @Test
    void testDeleteAllIngredients() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        ingredientRepo.save(new Ingredient());
        ingredientRepo.save(new Ingredient());
        ingredientRepo.save(new Ingredient());

        assertEquals(3, ingredientRepo.ingredients.size());

        ResponseEntity<Void> response =
                ingredientController.deleteAllIngredients();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        assertEquals(0, ingredientRepo.ingredients.size());
        assertTrue(ingredientRepo.calledMethods.contains("deleteAll"));

        verify(webSocketService)
                .publishIngredientListChanged(null);
    }


    @Test
    void testUpdateIngredientSuccess() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        Ingredient original = new Ingredient();
        original.setName("Potato");
        ingredientRepo.save(original);
        Long id = original.getId();

        Ingredient updated = new Ingredient();
        updated.setName("Sweet Potato");

        ResponseEntity<Ingredient> response = ingredientController.updateIngredient(id, updated);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Sweet Potato", response.getBody().getName());
        assertTrue(ingredientRepo.calledMethods.contains("findById"));
        assertTrue(ingredientRepo.calledMethods.contains("save"));

        verify(webSocketService)
                .publishIngredientListChanged(id);
    }

    @Test
    void testConstructor() {
        IngredientController controller = new IngredientController(ingredientService);

        assertNotNull(controller);
    }

    @Test
    void testMultipleOperationsSequence() {
        assertTrue(ingredientRepo.calledMethods.isEmpty());

        // Add ingredients
        Ingredient ing1 = new Ingredient();
        ing1.setName("Sugar");
        Ingredient ing2 = new Ingredient();
        ing2.setName("Flour");

        ingredientController.addIngredient(ing1);
        ingredientController.addIngredient(ing2);

        // Count
        ResponseEntity<Long> countResponse = ingredientController.countIngredients();
        assertEquals(2L, countResponse.getBody());

        // Get all
        ResponseEntity<List<Ingredient>> allResponse = ingredientController.getAllIngredients();
        assertEquals(2, allResponse.getBody().size());

        // Delete one
        ingredientController.deleteIngredient(1L);

        // Count again
        ResponseEntity<Long> countResponse2 = ingredientController.countIngredients();
        assertEquals(1L, countResponse2.getBody());

        // Verify repository methods were called
        assertTrue(ingredientRepo.calledMethods.contains("save"));
        assertTrue(ingredientRepo.calledMethods.contains("count"));
        assertTrue(ingredientRepo.calledMethods.contains("findAll"));
        assertTrue(ingredientRepo.calledMethods.contains("deleteById"));
    }
}