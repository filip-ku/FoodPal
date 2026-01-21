package server.Repository;

import commons.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient,Long> {

    /**
     * Checks if an ingredient with the given name exists.
     * @param name the ingredient name to check
     * @return true if an ingredient with this name exists, otherwise false
     */
    boolean existsByName(String name);

    /**
     * Finds ingredients whose names contain the given text (case-insensitive).
     * @param name the text to search for in ingredient names
     * @return list of matching ingredients (empty if none found)
     */
    List<Ingredient> findByNameContainingIgnoreCase(String name);
}
