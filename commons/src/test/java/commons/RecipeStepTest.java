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
        assertEquals("recipe cannot be null", e.getMessage());
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

    @Test
    void testSetAndGetPosition() {
        RecipeStep rs = new RecipeStep();
        rs.setPosition(10);
        assertEquals(10, rs.getPosition());
    }

    @Test
    void testSetAndGetInstruction() {
        RecipeStep rs = new RecipeStep();
        rs.setInstruction("Mix ingredients");
        assertEquals("Mix ingredients", rs.getInstruction());
    }

    @Test
    void testSetAndGetRecipe() {
        RecipeStep rs = new RecipeStep();
        Recipe recipe = new Recipe();
        rs.setRecipe(recipe);
        assertEquals(recipe, rs.getRecipe());
    }

    @Test
    void testToStringIsNotEmpty() {
        RecipeStep rs = new RecipeStep(new Recipe(), 1, "Bake it");
        assertFalse(rs.toString().isEmpty());
    }
}