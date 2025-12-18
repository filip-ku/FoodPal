package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {

    @Test
    void testNoArgsConstructor() {
        Recipe recipe = new Recipe();
        assertNotNull(recipe);
    }

    @Test
    void testSetNameThrowsForNull() {
        Recipe recipe = new Recipe();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipe.setTitle(null));
        assertEquals("Recipe title cannot be null", e.getMessage());
    }

    @Test
    void testDefaultIdIsNull() {
        Recipe recipe = new Recipe();
        assertNull(recipe.getId());
    }

    @Test
    void testSetAndGetTitle() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Chocolate Cake");
        assertEquals("Chocolate Cake", recipe.getTitle());
    }

    @Test
    void testToStringIsNotEmpty() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Test Recipe");
        assertFalse(recipe.toString().isEmpty());
    }

    @Test
    void testIngredientsListIsEmptyByDefault() {
        Recipe recipe = new Recipe();
        assertNotNull(recipe.getIngredients());
        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    void testStepsListIsEmptyByDefault() {
        Recipe recipe = new Recipe();
        assertNotNull(recipe.getSteps());
        assertTrue(recipe.getSteps().isEmpty());
    }

    @Test
    void testEqualsSameObject() {
        Recipe recipe = new Recipe();
        assertEquals(recipe, recipe);
    }

    @Test
    void testEqualsNull() {
        Recipe recipe = new Recipe();
        assertNotEquals(null, recipe);
    }

    @Test
    void testEqualsSameAttributes() {
        Recipe recipe1 = new Recipe("Test Recipe", new BigDecimal(3.0));
        Recipe recipe2 = new Recipe("Test Recipe", new BigDecimal(3.0));
        assertEquals(recipe1, recipe2);
    }

    @Test
    void testEqualsDifferentTitle() {
        Recipe recipe1 = new Recipe("Test Recipe", new BigDecimal(3.0));
        Recipe recipe2 = new Recipe("Different Test Recipe", new BigDecimal(3.0));
        assertNotEquals(recipe1, recipe2);
    }

    @Test
    void testEqualsDifferentServings() {
        Recipe recipe1 = new Recipe("Test Recipe", new BigDecimal(3.0));
        Recipe recipe2 = new Recipe("Test Recipe", new BigDecimal(4.0));
        assertNotEquals(recipe1, recipe2);
    }

    @Test
    void testHashCode() {
        Recipe recipe1 = new Recipe("Test Recipe");
        Recipe recipe2 = new Recipe("Test Recipe");
        assertEquals(recipe1.hashCode(), recipe2.hashCode());
    }

    @Test
    void testAddRecipeStepRelationship() {
        Recipe recipe = new Recipe();
        RecipeStep step = new RecipeStep();

        recipe.addStep(step);
        step.setPosition(1);

        assertEquals(recipe, step.getRecipe());
        assertEquals(1, step.getPosition());
    }

    @Test
    void testAddRecipeIngredientRelationship() {
        Recipe recipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        RecipeIngredient recipeIngredient = new RecipeIngredient();

        recipeIngredient.setIngredient(ingredient);
        recipe.addRecipeIngredient(recipeIngredient);
        recipeIngredient.setPosition(1);

        assertEquals(recipe, recipeIngredient.getRecipe());
        assertEquals(ingredient, recipeIngredient.getIngredient());
        assertEquals(1, recipeIngredient.getPosition());
    }
}