package server.Service;

import commons.Ingredient;
import commons.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.IngredientRepository;
import server.Repository.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
//    private final IngredientRepository ingredientRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
//        this.ingredientRepository = ingredientRepository;
    }

    public Recipe addRecipe(Recipe recipe) {
        if (isNullOrEmpty(recipe.getTitle())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return recipeRepository.save(recipe);
        }
    }

    public Recipe getRecipe(long id) {
        return recipeRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public void removeRecipe(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

//    public Recipe updateRecipe(long id, Recipe incoming) {
//        Recipe existing = recipeRepository
//                .findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//
//        if (incoming.getIngredients().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "Recipe must have at least one ingredient");
//        }
//
//        existing.setTitle(incoming.getTitle());
//        existing.setIngredients(incoming.getIngredients());
//
//        return recipeRepository.save(existing);
//    }

    /**
     * TODO
     * @param ingredient
     * @return
     */
    public Ingredient addIngredientToRecipe(Ingredient ingredient) {
        return null;
    }

    /**
     * checks if string is empty or null
     * @param s string
     * @return boolean
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
