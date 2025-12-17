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
    void testFullArgsConstructor() {
        Ingredient ingredient = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);
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

    @Test
    void testSetAndGetCarbsPer100g() {
        Ingredient ingredient = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);

        ingredient.setCarbsPer100g(5.0);
        assertEquals(5.0, ingredient.getCarbsPer100g());
    }

    @Test
    void testSetAndGetProteinPer100g() {
        Ingredient ingredient = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);

        ingredient.setProteinPer100g(7.0);
        assertEquals(7.0, ingredient.getProteinPer100g());
    }

    @Test
    void testSetAndGetFatPer100g() {
        Ingredient ingredient = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);

        ingredient.setFatPer100g(6.0);
        assertEquals(6.0, ingredient.getFatPer100g());
    }

    @Test
    void testSetAndGetForName() {
        Ingredient ingredient = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);

        ingredient.setName("New Test Ingredient");
        assertEquals("New Test Ingredient", ingredient.getName());
    }

    @Test
    void testEqualsSameObject() {
        Ingredient ingredient = new Ingredient();
        assertEquals(ingredient, ingredient);
    }

    @Test
    void testEqualsNull() {
        Ingredient ingredient = new Ingredient();
        assertNotEquals(ingredient, null);
    }

    @Test
    void testEqualsSameAttributes() {
        Ingredient ingredient1 = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);
        Ingredient ingredient2 = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);
        assertEquals(ingredient1, ingredient2);
    }

    @Test
    void testEqualsDifferentName() {
        Ingredient ingredient1 = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);
        Ingredient ingredient2 = new Ingredient(
                "Different Test Ingredient", 0.1, 0.2, 0.3);
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    void testEqualsDifferentProtein() {
        Ingredient ingredient1 = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);
        Ingredient ingredient2 = new Ingredient(
                "Test Ingredient", 0.2, 0.2, 0.3);
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    void testEqualsDifferentFat() {
        Ingredient ingredient1 = new Ingredient(
                "Test Ingredient", 0.1, 0.3, 0.3);
        Ingredient ingredient2 = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    void testEqualsDifferentCarbs() {
        Ingredient ingredient1 = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.1);
        Ingredient ingredient2 = new Ingredient(
                "Test Ingredient", 0.1, 0.2, 0.3);
        assertNotEquals(ingredient1, ingredient2);
    }

    @Test
    void testHashCode() {
        Ingredient ingredient1 = new Ingredient("Test Ingredient");
        Ingredient ingredient2 = new Ingredient("Test Ingredient");
        assertEquals(ingredient1.hashCode(), ingredient2.hashCode());
    }
}