package server.Service;

import commons.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.RecipeRepository;
import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe addRecipe(Recipe recipe) {
        if (isNullOrEmpty(recipe.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipe title cannot be empty");
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

    /**
     * checks if string is empty or null
     * @param s string
     * @return boolean
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
