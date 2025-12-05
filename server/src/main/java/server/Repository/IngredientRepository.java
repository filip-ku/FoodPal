package server.Repository;

import commons.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient,Long> {

    boolean existsByName(String name);

    List<Ingredient> findByNameContainingIgnoreCase(String name);
}
