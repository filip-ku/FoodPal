package server.Service;

import commons.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.IngredientRepository;
import server.Repository.RecipeRepository;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private static final Logger log = LoggerFactory.getLogger(IngredientService.class);
    private final RecipeRepository recipeRepository;

    /**
     * Constructs the service with the required repository
     * @param ingredientRepository the required repository
     * @param recipeRepository the repository of recipe
     */
    @Autowired
    public IngredientService(IngredientRepository ingredientRepository,
                             RecipeRepository recipeRepository){
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Adds a new ingredient
     * @param ingredient the ingredient to be added
     * @return the ingredient with an assigned ID
     */
    public Ingredient addIngredient(Ingredient ingredient){
        log.info("Adding ingredient {}",ingredient);
        if (ingredient == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ingredient name cannot be null");
        }

        validateIngredient(ingredient);

        if (ingredientRepository.existsByName(ingredient.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ingredient: " + ingredient.getName() + " already exists");
        }

        log.debug("Adding ingredient with id {}",ingredient.getId());
        return ingredientRepository.save(ingredient);
    }

    /**
     * Gets a single ingredient by the ID
     * @param id the ID of the ingredient
     * @return the ingredient name if found
     */
    public Ingredient getIngredient(long id){
        log.info("Getting ingredient with id {}",id);
        return findIngredient(id);
    }

    /**
     * Takes all ingredients
     * @return a list of all ingredients
     */
    public List<Ingredient> getAllIngredients(){
        return ingredientRepository.findAll();
    }

    /**
     * Deletes an ingredient by the ID
     * @param id the ID of the ingredient
     */
    @Transactional
    public void removeIngredient(Long id){
        log.info("Removing ingredient with id {}",id);
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Counts the number of ingredient that are in the system
     * @return count of all ingredients
     */
    public long countIngredients(){
        log.info("Count all ingredients");
        return ingredientRepository.count();
    }

    /**
     * Counts how many times a specific ingredient is used across all recipes.
     *
     * @param id of the ingredient to count usage for
     * @return the number of times the ingredient appears in all recipes
     */
    public long countUsageOfIngredient(long id){
        Ingredient ingredient = findIngredient(id);

        return recipeRepository.findAll().stream()
                .flatMap(recipe -> recipe.getIngredients().stream())
                .filter(ri -> {
                    Ingredient ing = ri.getIngredient();
                    return ing != null && ing.getId() != null
                            && ing.getId().equals(ingredient.getId());
                }).count();
    }

    /**
     * Updates an ingredient name from an ID
     * @param id the id of the ingredient to be updated
     * @param ingredient data that will be updated to
     * @return the updated ingredient
     */
    public Ingredient updateIngredient(long id, Ingredient ingredient){
        log.info("Updating ingredient with id {}", id);

        Ingredient existing = findIngredient(id);

        if (ingredient == null || ingredient.getName() == null || ingredient.getName().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "New ingredient name cannot be null or empty");
        }
        validateIngredient(ingredient);

        existing.setName(ingredient.getName());
        existing.setCarbsPer100g(ingredient.getCarbsPer100g());
        existing.setProteinPer100g(ingredient.getProteinPer100g());
        existing.setFatPer100g(ingredient.getFatPer100g());
        return ingredientRepository.save(existing);

    }

    /**
     * Deletes all ingredients
     */
    public void deleteAllIngredients(){
        log.info("Deleting all ingredients");
        ingredientRepository.deleteAll();
    }

    /**
     * searches for ingredients by name
     * @param query input for search
     * @return a list of ingredients based that contains query
     */
    public List<Ingredient> searchIngredients(String query){
        if (isNullOrEmpty(query)){
            return ingredientRepository.findAll();
        }
        return ingredientRepository.findByNameContainingIgnoreCase(query);
    }

    private void validateIngredient(Ingredient ingredient){
        if (isNullOrEmpty(ingredient.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ingredient name cannot be null or empty");
        }

        validateNonNegative(ingredient.getProteinPer100g(), "Protein per 100g");
        validateNonNegative(ingredient.getCarbsPer100g(), "Carbs per 100g");
        validateNonNegative(ingredient.getFatPer100g(), "Fats per 100g");

    }

    private void validateNonNegative(Double value, String name){
        if (value != null && value <0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "cannot be negative");
        }
    }

    private Ingredient findIngredient(long id){
        return ingredientRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Utility to check if a string is null or empty
     * @param s the string to check
     * @return true if is null or empty, else false
     */
    private static boolean isNullOrEmpty(String s){
        return s == null || s.isEmpty();
    }
}
