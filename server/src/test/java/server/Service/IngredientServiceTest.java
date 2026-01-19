package server.Service;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import server.Repository.TestIngredientRepository;
import server.Repository.TestRecipeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngredientServiceTest {

    private TestIngredientRepository ingredientRepo;
    private TestRecipeRepository recipeRepo;
    private IngredientService ingredientService;

    @BeforeEach
    void setup() {
        ingredientRepo = new TestIngredientRepository();
        recipeRepo = new TestRecipeRepository();
        ingredientService = new IngredientService(ingredientRepo, recipeRepo);
    }

    @Test
    void removingIngredientRemovesFromRecipesTest() {
        Ingredient tomato = new Ingredient("Tomato");
        tomato.setId(1L);
        ingredientRepo.save(tomato);

        Recipe pasta = new Recipe("Pasta");
        pasta.setId(10L);
        RecipeIngredient ri = new RecipeIngredient(pasta, tomato, 0);
        pasta.addRecipeIngredient(ri);
        recipeRepo.save(pasta);

        assertEquals(1, recipeRepo.findAll().get(0).getIngredients().size());

        ingredientService.removeIngredient(1L);

        assertEquals(0, recipeRepo.findAll().get(0).getIngredients().size());
        assertFalse(ingredientRepo.existsById(1L));
    }

    @Test
    void addIngredientNegativeNutrientsTest() {
        Ingredient ingredient = new Ingredient("Test Ingredient", -5.0, 10.0, 10.0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            ingredientService.addIngredient(ingredient);
        });

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("cannot be negative"));
    }

    @Test
    void countUsageOfIngredientTest() {
        Ingredient salt = new Ingredient("Salt");
        salt.setId(1L);
        ingredientRepo.save(salt);

        Recipe r1 = new Recipe("R1");
        r1.addRecipeIngredient(new RecipeIngredient(r1, salt, 0));
        recipeRepo.save(r1);

        Recipe r2 = new Recipe("R2");
        r2.addRecipeIngredient(new RecipeIngredient(r2, salt, 0));
        recipeRepo.save(r2);

        recipeRepo.save(new Recipe("R3"));

        long usage = ingredientService.countUsageOfIngredient(1L);
        assertEquals(2, usage);
    }

    @Test
    void searchIngredientsCaseInsensitiveTest() {
        ingredientRepo.save(new Ingredient("Carrot"));
        ingredientRepo.save(new Ingredient("Broccoli"));

        List<Ingredient> results = ingredientService.searchIngredients("rro");

        assertEquals(1, results.size());
        assertEquals("Carrot", results.get(0).getName());
    }

    @Test
    void ingredientCalorieCalculationTest() {
        Ingredient ing = new Ingredient("Test", 10.0, 5.0, 20.0);

        assertEquals(165.0, ing.getCalories());
    }

    @Test
    void addIngredientFailsWhenNameIsNullTest() {
        Ingredient ing = new Ingredient();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            ingredientService.addIngredient(ing);
        });

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void updateIngredientFailsWhenNotFoundTest() {
        Ingredient updateData = new Ingredient("New Name");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            ingredientService.updateIngredient(999L, updateData);
        });

        assertEquals(404, ex.getStatusCode().value());
    }
}