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

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository){
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient addIngredient(Ingredient ingredient){
        log.info("Adding ingredient {}",ingredient);
        if (isNullOrEmpty(ingredient.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient name cannot be null or empty");
        }
        else {
            log.debug("Adding ingredient with id {}",ingredient.getId());
            return ingredientRepository.save(ingredient);
        }
    }

    public Ingredient getIngredient(long id){
        log.info("Getting ingredient with id {}",id);
        return ingredientRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Ingredient> getAllIngredients(){
        return ingredientRepository.findAll();
    }

    public void removeIngredient(Long id){
        log.info("Removing ingredient with id {}",id);
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public long countIngredients(){
        log.info("Count all ingredients");
        return ingredientRepository.count();
    }

    public Ingredient updateIngredient(long id, Ingredient ingredient){
        log.info("Updating ingredient with id {}", id);
        Ingredient existing = getIngredient(id);
        existing.setName(ingredient.getName());
        return ingredientRepository.save(existing);

    }

    private static boolean isNullOrEmpty(String s){
        return s == null || s.isEmpty();
    }
}
