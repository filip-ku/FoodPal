package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    @Test
    void testNoArgsConstructor() {
        Ingredient ingredient = new Ingredient();
        assertNotNull(ingredient);
    }

    @Test
    void testSetNameThrowsForNull() {
        Ingredient ingredient = new Ingredient();
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> ingredient.setName(null));
        assertEquals("Ingredient name cannot be null", e.getMessage());
    }

    @Test
    void testDefaultIdIsNull() {
        Ingredient ingredient = new Ingredient();
        assertNull(ingredient.getId());
    }

}