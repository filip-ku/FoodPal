package server.Service;

import commons.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.IngredientRepository;
import server.Repository.RecipeRepository;
import server.ws.WebSocketService;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private static final Logger log = LoggerFactory.getLogger(IngredientService.class);
    private final RecipeRepository recipeRepository;
    private final WebSocketService webSocketService;

    /**
     * Constructs the service with the required repository
     * @param ingredientRepository the required repository
     * @param recipeRepository the repository of recipe
     * @param webSocketService the WebSocket service for publishing events
     */
    @Autowired
    public IngredientService(IngredientRepository ingredientRepository,
                             RecipeRepository recipeRepository, WebSocketService webSocketService){
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.webSocketService = webSocketService;
    }

    /**
     * Adds a new ingredient
     * @param ingredient the ingredient to be added
     * @return the ingredient with an assigned ID
     */
    @Transactional
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
        Ingredient saved = ingredientRepository.save(ingredient);
        webSocketService.publishIngredientListChanged(saved.getId());
        return saved;
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
            webSocketService.publishIngredientListChanged(id);
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
    @Transactional
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
        Ingredient updatedIngredient = ingredientRepository.save(existing);
        runAfterCommit(() -> webSocketService.
                publishIngredientListChanged(updatedIngredient.getId()));
        return updatedIngredient;

    }

    /**
     * Executes the given action after the current transaction commits.
     * If no transaction is active, executes immediately.
     *
     * @param action the action to run after commit
     */
    private void runAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }

    /**
     * Deletes all ingredients
     */
    @Transactional
    public void deleteAllIngredients(){
        log.info("Deleting all ingredients");
        ingredientRepository.deleteAll();
        webSocketService.publishIngredientListChanged(null);
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
