package commons;

import org.junit.jupiter.api.Test;

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

}