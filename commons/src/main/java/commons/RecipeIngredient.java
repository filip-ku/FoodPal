package commons;

public class RecipeIngredient {

    private Ingredient ingredient;
    private int quantity;
    private String units;
    private String notes;

    public RecipeIngredient(Ingredient ingredient, int quantity, String units, String notes) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.units = units;
        this.notes = notes;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnits() {
        return units;
    }

    public String getNotes() {
        return notes;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return ingredient.getName()
                + " " + quantity + units
                + (notes == null || notes.isBlank() ? "" : "  - note: " + notes);
    }
}
