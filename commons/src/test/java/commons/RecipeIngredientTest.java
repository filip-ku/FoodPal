package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientTest {

    @Test
    void testNoArgsConstructor() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        assertNotNull(recipeIngredient);
    }

    @Test
    void testConstructorInitializesRequiredFieldsAndLeavesOptionalsNull() {
        RecipeIngredient ri = new RecipeIngredient(new Recipe(), new Ingredient(), 3);

        assertEquals(3, ri.getPosition());

        assertNull(ri.getAmount());
        assertNull(ri.getUnit());
        assertNull(ri.getInformalAmount());
        assertNull(ri.getNote());
    }

    @Test
    void testSetRecipeIdThrowsForNull() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeIngredient.setRecipe(null));
        assertEquals("recipe cannot be null", e.getMessage());
    }

    @Test
    void testSetIngredientIdThrowsForNull() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeIngredient.setIngredient(null));
        assertEquals("ingredient cannot be null", e.getMessage());
    }

    @Test
    void testDefaultIdIsNull() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        assertNull(recipeIngredient.getId());
    }

    @Test
    void testSetAmountThrowsForLessThanZero() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeIngredient.setAmount(-1.0));
        assertEquals("amount cannot be negative", e.getMessage());
    }

    @Test
    void testSetPositionThrowsForLessThanZero() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeIngredient.setPosition(-1));
        assertEquals("position cannot be negative", e.getMessage());
    }

}