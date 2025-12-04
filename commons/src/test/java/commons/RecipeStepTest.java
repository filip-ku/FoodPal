package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeStepTest {

    @Test
    void testNoArgsConstructor() {
        RecipeStep recipeStep = new RecipeStep();
        assertNotNull(recipeStep);
    }

    @Test
    void testConstructorInitializesRequiredFieldsAndLeavesOptionalsNull() {
        RecipeStep rs = new RecipeStep(new Recipe(), 5, "Preheat the oven");

        assertEquals(5, rs.getPosition());
        assertEquals("Preheat the oven", rs.getInstruction());

    }

    @Test
    void testSetRecipeIdThrowsForNull() {
        RecipeStep recipeStep = new RecipeStep();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeStep.setRecipe(null));
        assertEquals("recipeId cannot be null", e.getMessage());
    }

    @Test
    void testDefaultIdIsNull() {
        RecipeStep recipeStep = new RecipeStep();
        assertNull(recipeStep.getId());
    }

    @Test
    void testSetPositionThrowsForLessThanZero() {
        RecipeStep recipeStep = new RecipeStep();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeStep.setPosition(-1));
        assertEquals("position cannot be negative", e.getMessage());
    }

    @Test
    void testSetInstructionThrowsForNull() {
        RecipeStep recipeStep = new RecipeStep();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> recipeStep.setInstruction(null));
        assertEquals("instruction cannot be null", e.getMessage());
    }

}