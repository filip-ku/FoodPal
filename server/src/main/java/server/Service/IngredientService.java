package server.Service;

import commons.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.IngredientRepository;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private static final Logger log = LoggerFactory.getLogger(IngredientService.class);

    /**
     * Constructs the service with the required repository
     * @param ingredientRepository the required repository
     */
    @Autowired
    public IngredientService(IngredientRepository ingredientRepository){
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Adds a new ingredient
     * @param ingredient the ingredient to be added
     * @return the ingredient with an assigned ID
     */
    public Ingredient addIngredient(Ingredient ingredient){
        log.info("Adding ingredient {}",ingredient);
        if (isNullOrEmpty(ingredient.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ingredient name cannot be null or empty");
        }
        else {
            log.debug("Adding ingredient with id {}",ingredient.getId());
            return ingredientRepository.save(ingredient);
        }
    }

    /**
     * Gets a single ingredient by the ID
     * @param id the ID of the ingredient
     * @return the ingredient name if found
     */
    public Ingredient getIngredient(long id){
        log.info("Getting ingredient with id {}",id);
        return ingredientRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
     * Updates an ingredient name from an ID
     * @param id the id of the ingredient to be updated
     * @param ingredient data that will be updated to
     * @return the updated ingredient
     */
    public Ingredient updateIngredient(long id, Ingredient ingredient){
        log.info("Updating ingredient with id {}", id);

        // This will throw NOT_FOUND exception if ingredient doesn't exist
        Ingredient existing = getIngredient(id);

        if (isNullOrEmpty(ingredient.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "New ingredient name cannot be null or empty");
        }

        existing.setName(ingredient.getName());
        existing.setCarbsPer100g(ingredient.getCarbsPer100g());
        existing.setFatPer100g(ingredient.getFatPer100g());
        existing.setProteinPer100g(ingredient.getProteinPer100g());

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
     * Utility to check if a string is null or empty
     * @param s the string to check
     * @return true if is null or empty, else false
     */
    private static boolean isNullOrEmpty(String s){
        return s == null || s.isEmpty();
    }
}
