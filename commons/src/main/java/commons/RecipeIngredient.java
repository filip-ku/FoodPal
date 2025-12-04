package commons;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Objects;

@Entity
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // reference to the owning recipe
    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    // reference to the ingredient entity
    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id", referencedColumnName = "id", nullable = false)
    private Ingredient ingredient;

    // amount of the ingredient (nullable if informal)
    @Column
    private Double amount;

    // unit for the amount (nullable if informal)
    @Column(length = 20)
    private String unit;

    // informal amount like "a pinch" (nullable if formal)
    @Column(length = 100)
    private String informalAmount;

    // order in the recipe ingredient list
    @Column(nullable = false)
    private int position;

    // optional text note
    @Column(length = 200)
    private String note;

    public RecipeIngredient() {
        // for object mapping
    }

    // AI-generated
    public RecipeIngredient(Recipe recipe, Ingredient ingredient, int position) {
        this.setRecipe(recipe);
        this.setIngredient(ingredient);
        this.setPosition(position);
    }

    public Long getId() {
        return id;
    }

    // AI-generated
    /**
     * Returns the recipe this ingredient belongs to.
     *
     * @return the owning recipe.
     */
    public Recipe getRecipe() {
        return recipe;
    }

    // AI-generated
    /**
     * Sets the recipe this ingredient belongs to.
     *
     * @param recipe the owning recipe, must not be null.
     */
    public void setRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("recipe cannot be null");
        }
        this.recipe = recipe;
    }

    // AI-generated
    /**
     * Returns the ingredient entity for this recipe ingredient.
     *
     * @return the ingredient entity.
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    // AI-generated
    /**
     * Sets the ingredient entity for this recipe ingredient.
     *
     * @param ingredient the ingredient entity, must not be null.
     */
    public void setIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("ingredient cannot be null");
        }
        this.ingredient = ingredient;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        if (amount != null && amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getInformalAmount() {
        return informalAmount;
    }

    public void setInformalAmount(String informalAmount) {
        this.informalAmount = informalAmount;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        this.position = position;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredient that = (RecipeIngredient) o;
        return position == that.position
                && Objects.equals(id, that.id)
                && Objects.equals(amount, that.amount)
                && Objects.equals(unit, that.unit)
                && Objects.equals(informalAmount, that.informalAmount)
                && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, unit, informalAmount, position, note);
    }
}