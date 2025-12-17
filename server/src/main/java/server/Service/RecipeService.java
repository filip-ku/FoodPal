package server.Service;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Business-logic layer for {@link Recipe} objects.
 */
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe addRecipe(Recipe recipe) {
        if (isNullOrEmpty(recipe.getTitle())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Recipe title cannot be empty"
            );
        }
        return recipeRepository.save(recipe);
    }

    public Recipe getRecipe(Long id) {
        return findRecipe(id);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public void removeRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        recipeRepository.deleteById(id);
    }

    public Recipe updateRecipe(Recipe recipe) {
        if (isNullOrEmpty(recipe.getTitle())
                || recipe.getId() == null
                || !recipeRepository.existsById(recipe.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Recipe title cannot be empty"
            );
        }
        return recipeRepository.save(recipe);
    }

    public Recipe addIngredientToRecipe(long recipeId, RecipeIngredient recipeIngredient) {
        Recipe recipe = findRecipe(recipeId);

        if (recipe.getIngredients().contains(recipeIngredient)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Recipe ingredient already exists"
            );
        }

        recipeIngredient.setRecipe(recipe);
        recipe.addRecipeIngredient(recipeIngredient);
        return recipeRepository.save(recipe);
    }

    public Recipe addStepToRecipe(long recipeId, RecipeStep recipeStep) {
        Recipe recipe = findRecipe(recipeId);

        recipeStep.setRecipe(recipe);
        recipe.addStep(recipeStep);
        return recipeRepository.save(recipe);
    }

    public Recipe deleteIngredientFromRecipe(long recipeId, RecipeIngredient recipeIngredient) {
        Recipe recipe = findRecipe(recipeId);
        recipe.getIngredients().remove(recipeIngredient);
        return recipeRepository.save(recipe);
    }

    public Recipe deleteStepFromRecipe(long recipeId, RecipeStep recipeStep) {
        Recipe recipe = findRecipe(recipeId);
        recipe.getSteps().remove(recipeStep);
        return recipeRepository.save(recipe);
    }

    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        Recipe recipe = getRecipe(recipeId);
        return new ArrayList<>(recipe.getIngredients());
    }

    public List<RecipeStep> getStepsForRecipe(long recipeId) {
        Recipe recipe = getRecipe(recipeId);
        return new ArrayList<>(recipe.getSteps());
    }

    public Recipe removeIngredientFromRecipe(long recipeId, long recipeIngredientId) {
        Recipe recipe = getRecipe(recipeId);
        recipe.getIngredients().removeIf(ingredient ->
                ingredient.getId() != null
                        && ingredient.getId().equals(recipeIngredientId));
        return updateRecipe(recipe);
    }

    public Recipe removeStepFromRecipe(long recipeId, long stepId) {
        Recipe recipe = getRecipe(recipeId);
        recipe.getSteps().removeIf(step ->
                step.getId() != null && step.getId().equals(stepId));
        return updateRecipe(recipe);
    }

    private Recipe findRecipe(long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
