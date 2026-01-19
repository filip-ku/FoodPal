package server.Controller;

import commons.Recipe;
import commons.RecipeStep;
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
 * Unit tests focused on RecipeSteps behavior via RecipeService
 * (add/get/update/delete + not-found cases).
 */
public class RecipeStepsTest {

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
    void addStepToRecipe_appendsStep_andPublishesContentChanged() {
        // Arrange
        Recipe recipe = recipeRepo.save(new Recipe("Pasta"));

        RecipeStep step = new RecipeStep();
        step.setPosition(1);
        step.setInstruction("Boil water");

        // Act
        Recipe updated = recipeService.addStepToRecipe(recipe.getId(), step);

        // Assert
        assertNotNull(updated);
        assertEquals(recipe.getId(), updated.getId());
        assertEquals(1, updated.getSteps().size());

        RecipeStep savedStep = updated.getSteps().get(0);
        assertEquals(1, savedStep.getPosition());
        assertEquals("Boil water", savedStep.getInstruction());

        // Service should associate recipe
        assertNotNull(savedStep.getRecipe());
        assertEquals(recipe.getId(), savedStep.getRecipe().getId());

        assertTrue(recipeRepo.calledMethods.contains("findById"));
        assertTrue(recipeRepo.calledMethods.contains("save"));
        verify(webSocketService).publishRecipeContentChanged(recipe.getId());
    }

    @Test
    void getStepsForRecipe_returnsCopyOfStepsList() {
        // Arrange
        Recipe recipe = recipeRepo.save(new Recipe("Cake"));

        RecipeStep s1 = new RecipeStep();
        s1.setPosition(1);
        s1.setInstruction("Mix");

        RecipeStep s2 = new RecipeStep();
        s2.setPosition(2);
        s2.setInstruction("Bake");

        recipeService.addStepToRecipe(recipe.getId(), s1);
        recipeService.addStepToRecipe(recipe.getId(), s2);

        // Act
        List<RecipeStep> steps = recipeService.getStepsForRecipe(recipe.getId());

        // Assert
        assertEquals(2, steps.size());
        assertEquals("Mix", steps.get(0).getInstruction());
        assertEquals("Bake", steps.get(1).getInstruction());

        // Ensure it's a copy
        steps.clear();
        List<RecipeStep> stepsAgain = recipeService.getStepsForRecipe(recipe.getId());
        assertEquals(2, stepsAgain.size());
    }

    @Test
    void removeStepFromRecipe_removesMatchingId_andPublishesContentChanged() {
        // Arrange
        Recipe recipe = recipeRepo.save(new Recipe("Soup"));

        RecipeStep s1 = new RecipeStep();
        s1.setPosition(1);
        s1.setInstruction("Chop vegetables");

        RecipeStep s2 = new RecipeStep();
        s2.setPosition(2);
        s2.setInstruction("Simmer");

        Recipe updated = recipeService.addStepToRecipe(recipe.getId(), s1);
        updated = recipeService.addStepToRecipe(recipe.getId(), s2);

        // Ensure step IDs exist for remove-by-id logic
        updated.getSteps().get(0).setId(10L);
        updated.getSteps().get(1).setId(20L);
        recipeRepo.save(updated);

        // Key line: ignore publish calls made during Arrange
        clearInvocations(webSocketService);

        // Act
        Recipe afterRemoval = recipeService.removeStepFromRecipe(recipe.getId(), 10L);

        // Assert
        assertEquals(1, afterRemoval.getSteps().size());
        assertEquals(20L, afterRemoval.getSteps().get(0).getId());

        verify(webSocketService, times(1)).publishRecipeContentChanged(recipe.getId());
    }

    @Test
    void updateStepInRecipe_updatesPositionAndInstruction_andPublishesContentChanged() {
        // Arrange
        Recipe recipe = recipeRepo.save(new Recipe("Rice"));

        RecipeStep original = new RecipeStep();
        original.setPosition(1);
        original.setInstruction("Rinse rice");

        Recipe updated = recipeService.addStepToRecipe(recipe.getId(), original);

        RecipeStep persisted = updated.getSteps().get(0);
        persisted.setId(5L);
        recipeRepo.save(updated);

        RecipeStep patch = new RecipeStep();
        patch.setPosition(2);
        patch.setInstruction("Cook rice");

        // Key line: ignore publish calls made during Arrange
        clearInvocations(webSocketService);

        // Act
        RecipeStep saved = recipeService.updateStepInRecipe(recipe.getId(), 5L, patch);

        // Assert
        assertNotNull(saved);
        assertEquals(5L, saved.getId());
        assertEquals(2, saved.getPosition());
        assertEquals("Cook rice", saved.getInstruction());

        verify(webSocketService, times(1)).publishRecipeContentChanged(recipe.getId());
        assertTrue(recipeRepo.calledMethods.contains("save"));
    }

    @Test
    void updateStepInRecipe_throws404_whenStepNotFound() {
        // Arrange
        Recipe recipe = recipeRepo.save(new Recipe("Burger"));

        RecipeStep patch = new RecipeStep();
        patch.setPosition(99);
        patch.setInstruction("Nope");

        // Act + Assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.updateStepInRecipe(recipe.getId(), 999L, patch)
        );

        assertEquals(404, ex.getStatusCode().value());
        assertEquals("Step not found", ex.getReason());
        verify(webSocketService, never()).publishRecipeContentChanged(anyLong());
    }

    @Test
    void addStepToRecipe_throws404_whenRecipeNotFound() {
        // Arrange
        RecipeStep step = new RecipeStep();
        step.setPosition(1);
        step.setInstruction("Doesn't matter");

        // Act + Assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.addStepToRecipe(999L, step)
        );

        assertEquals(404, ex.getStatusCode().value());
        verify(webSocketService, never()).publishRecipeContentChanged(anyLong());
    }

    @Test
    void getStepsForRecipe_throws404_whenRecipeNotFound() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.getStepsForRecipe(999L)
        );

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void addStepToRecipe_nonExistingRecipe_throwsNotFound() {
        RecipeStep step = new RecipeStep();
        step.setPosition(1);
        step.setInstruction("Do something");

        var ex = assertThrows(ResponseStatusException.class,
                () -> recipeService.addStepToRecipe(999L, step));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(recipeRepo.calledMethods.contains("findById"));
        verify(webSocketService, never()).publishRecipeContentChanged(anyLong());
    }

    @Test
    void getStepsForRecipe_nonExistingRecipe_throwsNotFound() {
        var ex = assertThrows(ResponseStatusException.class,
                () -> recipeService.getStepsForRecipe(999L));

        assertEquals(404, ex.getStatusCode().value());
        verify(webSocketService, never()).publishRecipeContentChanged(anyLong());
    }
}