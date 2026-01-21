package server.Controller;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.TestRecipeRepository;
import server.Service.RecipeService;
import server.ws.WebSocketService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class which is used to test {@link RecipeController}
 */
public class RecipeControllerTest {

    private TestRecipeRepository recipeRepo;

    private RecipeService recipeService;
    private WebSocketService webSocketService;


    @BeforeEach
    public void setUp() {
        recipeRepo = new TestRecipeRepository();
        webSocketService = mock(WebSocketService.class);
        recipeService = new RecipeService(recipeRepo, webSocketService);
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
        verify(webSocketService).publishRecipeListChange(savedRecipe.getId());
        verify(webSocketService).
                publishRecipeChanged("Create", savedRecipe.getId(), savedRecipe.getTitle());
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
        verify(webSocketService).publishRecipeListChange(recipe.getId());
        verify(webSocketService).publishRecipeChanged("Deleted", recipe.getId(), null);
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

    @Test
    void updateRecipePublishesChange() {
        Recipe recipe = recipeRepo.save(new Recipe("Old"));
        recipe.setTitle("New");

        Recipe saved = recipeService.updateRecipe(recipe);

        assertEquals("New", saved.getTitle());
        verify(webSocketService).publishRecipeChanged("Changed", saved.getId(), "New");
    }

}
