package server.Controller;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import server.Service.RecipeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class which is used to test {@link RecipeController}
 */
public class RecipeControllerTest {

    private TestRecipeRepository recipeRepo;

    private RecipeService recipeService;

    @BeforeEach
    public void setUp() {
        recipeRepo = new TestRecipeRepository();
        recipeService = new RecipeService(recipeRepo);
    }

    @Test
    void addRecipeSuccess() {
        assertTrue(recipeRepo.calledMethods.isEmpty());
        Recipe recipe = new Recipe("Pizza");

        Recipe savedRecipe = recipeService.addRecipe(recipe);

        assertNotNull(savedRecipe.getId());
        assertEquals("Pizza", savedRecipe.getTitle());
        assertTrue(recipeRepo.calledMethods.contains("save"));
        assertEquals(1, recipeRepo.recipes.size());
    }

    @Test
    void addRecipeEmptyTitle() {
        assertTrue(recipeRepo.calledMethods.isEmpty());
        Recipe recipe = new Recipe("");

        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.addRecipe(recipe)
        );

        assertEquals(400, e.getStatusCode().value());
        assertEquals("Recipe title cannot be empty", e.getReason());

        assertTrue(recipeRepo.calledMethods.isEmpty());
    }

    @Test
    void getRecipeSuccess() {
        assertTrue(recipeRepo.calledMethods.isEmpty());
        Recipe recipe = new Recipe("Soup");
        recipeRepo.save(recipe);
        assertTrue(recipeRepo.calledMethods.contains("save"));

        Recipe result = recipeService.getRecipe(recipe.getId());
        assertEquals("Soup", result.getTitle());
        assertTrue(recipeRepo.calledMethods.contains("findById"));
    }

    @Test
    void getRecipeNotFound() {
        assertTrue(recipeRepo.calledMethods.isEmpty());
        ResponseStatusException e = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.getRecipe(99L)
        );

        assertEquals(404, e.getStatusCode().value());
        assertTrue(recipeRepo.calledMethods.contains("findById"));
    }

    @Test
    void getAllRecipes() {
        assertTrue(recipeRepo.calledMethods.isEmpty());
        List<Recipe> recipes = recipeService.getAllRecipes();
        assertEquals(0, recipes.size());
        assertEquals("findAll", recipeRepo.calledMethods.get(0));

        recipeRepo.save(new Recipe("Pizza"));
        recipeRepo.save(new Recipe("Pasta"));

        recipes = recipeService.getAllRecipes();

        assertEquals(2, recipes.size());
        assertEquals("findAll", recipeRepo.calledMethods.get(3));
    }

    @Test
    void removeRecipeSuccess() {
        assertTrue(recipeRepo.calledMethods.isEmpty());
        Recipe recipe = new Recipe("Cake");
        recipeRepo.save(recipe);

        recipeService.removeRecipe(recipe.getId());

        assertEquals(0, recipeRepo.recipes.size());
        assertTrue(recipeRepo.calledMethods.contains("deleteById"));
    }

    @Test
    void removeRecipeNotFound() {
        assertTrue(recipeRepo.calledMethods.isEmpty());
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.removeRecipe(123L)
        );

        assertEquals(404, ex.getStatusCode().value());

        assertFalse(recipeRepo.calledMethods.contains("deleteById"));
    }

}
