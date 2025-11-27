package server.Service;

import commons.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.IngredientRepository;
import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository){
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient addIngredient(Ingredient ingredient){
        if (isNullOrEmpty(ingredient.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient name cannot be null or empty");
        }
        else {
            return ingredientRepository.save(ingredient);
        }
    }

    public Ingredient getIngredient(long id){
        return ingredientRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Ingredient> getAllIngredients(){
        return ingredientRepository.findAll();
    }

    public void removeIngredient(Long id){
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private static boolean isNullOrEmpty(String s){
        return s == null || s.isEmpty();
    }

}
