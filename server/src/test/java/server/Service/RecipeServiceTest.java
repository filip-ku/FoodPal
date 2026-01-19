package server.Service;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.TestRecipeRepository;
import server.ws.WebSocketService;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeIngredientServiceTest {

    private TestRecipeRepository recipeRepo;
    private WebSocketService webSocketService;
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        recipeRepo = new TestRecipeRepository();
        webSocketService = mock(WebSocketService.class);
        recipeService = new RecipeService(recipeRepo, webSocketService);
    }

    @Test
    void addDuplicateIngredientToRecipeThrowsExceptionTest() {
        Recipe recipe = new Recipe("Soup");
        recipe.setId(1L);
        recipeRepo.save(recipe);

        Ingredient water = new Ingredient("Water");
        water.setId(5L);

        RecipeIngredient ri = new RecipeIngredient(recipe, water, 0);
        recipeService.addIngredientToRecipe(1L, ri);

        assertThrows(ResponseStatusException.class, () -> {
            recipeService.addIngredientToRecipe(1L, ri);
        });
    }

    @Test
    void updateRecipeIngredientTest() throws Exception {
        Recipe recipe = new Recipe("Cake");
        recipe.setId(1L);
        recipeRepo.save(recipe);

        Ingredient sugar = new Ingredient("Sugar");
        sugar.setId(5L);

        RecipeIngredient ri = new RecipeIngredient(recipe, sugar, 0);
        ri.setAmount(100.0);
        ri.setUnit("grams");

        Field idField = RecipeIngredient.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(ri, 100L);

        recipe.addRecipeIngredient(ri);
        recipeRepo.save(recipe);

        RecipeIngredient patch = new RecipeIngredient();
        patch.setAmount(200.0);
        patch.setUnit("kg");

        Recipe updatedRecipe = recipeService.updateRecipeIngredient(1L, 100L, patch);

        RecipeIngredient updatedRi = updatedRecipe.getIngredients().stream()
                .filter(i -> i.getId().equals(100L))
                .findFirst()
                .orElseThrow();

        assertEquals(200.0, updatedRi.getAmount());
        assertEquals("kg", updatedRi.getUnit());
        verify(webSocketService).publishRecipeContentChanged(1L);
    }

    @Test
    void testUpdateRecipeFailsWhenTitleIsMissing() {
        Recipe recipe = new Recipe("Old Title");
        recipe.setId(1L);
        recipeRepo.save(recipe);

        Recipe patch = new Recipe("");
        patch.setId(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            recipeService.updateRecipe(patch);
        });

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("Recipe title cannot be empty", ex.getReason());
    }

    @Test
    void testRemoveRecipeIngredientPublishesEvent() throws Exception {
        Recipe recipe = new Recipe("Salad");
        recipe.setId(1L);
        RecipeIngredient ri = new RecipeIngredient(recipe, new Ingredient("Lettuce"), 0);

        Field idField = RecipeIngredient.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(ri, 50L);

        recipe.addRecipeIngredient(ri);
        recipeRepo.save(recipe);

        recipeService.removeIngredientFromRecipe(1L, 50L);

        assertTrue(recipeRepo.findById(1L).get().getIngredients().isEmpty());
        verify(webSocketService).publishRecipeContentChanged(1L);
    }

    @Test
    void recipeStepRelationshipTest() {
        Recipe recipe = new Recipe("Pasta");
        RecipeStep step = new RecipeStep();
        step.setInstruction("Boil water");

        recipe.addStep(step);

        assertNotNull(step.getRecipe());
        assertEquals("Pasta", step.getRecipe().getTitle());
        assertTrue(recipe.getSteps().contains(step));
    }

    @Test
    void deleteIngredientFromNonExistentRecipeThrows404Test() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            recipeService.removeIngredientFromRecipe(999L, 1L);
        });

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void updateNonExistentStepThrows404Test() {
        Recipe recipe = new Recipe("Pasta");
        recipe.setId(1L);
        recipeRepo.save(recipe);

        RecipeStep patch = new RecipeStep();
        patch.setInstruction("New instruction");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            recipeService.updateStepInRecipe(1L, 500L, patch);
        });

        assertEquals(404, ex.getStatusCode().value());
        assertEquals("Step not found", ex.getReason());
    }
}