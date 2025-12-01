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
        RecipeIngredient ri = new RecipeIngredient(1L, 2L, 3);

        assertEquals(1L, ri.getRecipeId());
        assertEquals(2L, ri.getIngredientId());
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
                () -> recipeIngredient.setRecipeId(null));
        assertEquals("recipeId cannot be null", e.getMessage());
    }

    @Test
    void testSetIngredientIdThrowsForNull() {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeIngredient.setIngredientId(null));
        assertEquals("ingredientId cannot be null", e.getMessage());
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