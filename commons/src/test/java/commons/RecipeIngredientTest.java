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

    @Test
    void testSetAndGetUnit() {
        RecipeIngredient ri = new RecipeIngredient();
        String unit = "Tablespoon";
        ri.setUnit(unit);
        assertEquals(unit, ri.getUnit());
    }

    @Test
    void testSetAndGetInformalAmount() {
        RecipeIngredient ri = new RecipeIngredient();
        String text = "A handful";
        ri.setInformalAmount(text);
        assertEquals(text, ri.getInformalAmount());
    }

    @Test
    void testSetAndGetNote() {
        RecipeIngredient ri = new RecipeIngredient();
        String note = "Use organic if possible";
        ri.setNote(note);
        assertEquals(note, ri.getNote());
    }

    @Test
    void testSetAndGetAmountValid() {
        RecipeIngredient ri = new RecipeIngredient();
        Double val = 50.5;
        ri.setAmount(val);
        assertEquals(val, ri.getAmount());
    }

    @Test
    void testAmountCanBeZero() {
        RecipeIngredient ri = new RecipeIngredient();
        ri.setAmount(0.0);
        assertEquals(0.0, ri.getAmount());
    }

    @Test
    void testPositionCanBeZero() {
        RecipeIngredient ri = new RecipeIngredient();
        // 0 is not negative, so it should not throw exception
        ri.setPosition(0);
        assertEquals(0, ri.getPosition());
    }

    @Test
    void testAmountAcceptsNull() {
        RecipeIngredient ri = new RecipeIngredient();
        // Ensure that passing null doesn't throw an exception (unlike setRecipe)
        ri.setAmount(null);
        assertNull(ri.getAmount());
    }

    @Test
    void testEqualsSymmetric() {
        RecipeIngredient ri1 = new RecipeIngredient();
        RecipeIngredient ri2 = new RecipeIngredient();

        // Two empty objects should be equal
        assertEquals(ri1, ri2);
        assertEquals(ri2, ri1);
    }

    @Test
    void testNotEqualNull() {
        RecipeIngredient ri = new RecipeIngredient();
        assertNotEquals(null,ri);
    }

    @Test
    void testNotEqualDifferentClass() {
        RecipeIngredient ri = new RecipeIngredient();
        String notARecipeIngredient = "I am a String";
        assertNotEquals(notARecipeIngredient,ri);
    }

    @Test
    void testHashCodeConsistency() {
        RecipeIngredient ri1 = new RecipeIngredient();
        RecipeIngredient ri2 = new RecipeIngredient();
        assertEquals(ri1.hashCode(), ri2.hashCode());
    }

    @Test
    void testConstructorSetsFieldsCorrectly() {
        Recipe r = new Recipe();
        Ingredient i = new Ingredient();
        int pos = 10;

        RecipeIngredient ri = new RecipeIngredient(r, i, pos);

        assertSame(r, ri.getRecipe());
        assertSame(i, ri.getIngredient());
        assertEquals(pos, ri.getPosition());
    }

}