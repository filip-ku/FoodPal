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
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Recipe recipe;

    // reference to the ingredient entity
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
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

    /**
     * Default constructor for JPA and object mapping frameworks.
     */
    public RecipeIngredient() {
        // for object mapping
    }

    /**
     * Creates a new recipe ingredient for the given recipe and ingredient.
     *
     * @param recipe     the recipe this ingredient belongs to, must not be {@code null}
     * @param ingredient the ingredient entity, must not be {@code null}
     * @param position   the zero-based index of this
     *                   ingredient within the recipe; must be non-negative
     * @throws IllegalArgumentException if {@code recipe} or {@code ingredient}
     * is {@code null}, or if {@code position} is negative
     */
    public RecipeIngredient(Recipe recipe, Ingredient ingredient, int position) {
        this.setRecipe(recipe);
        this.setIngredient(ingredient);
        this.setPosition(position);
    }

    /**
     * Returns the unique identifier of this recipe ingredient.
     *
     * @return the id of this recipe ingredient, or {@code null} if it has not been persisted yet
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the recipe this ingredient belongs to.
     *
     * @return the owning recipe, never {@code null}
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe this ingredient belongs to.
     *
     * @param recipe the owning recipe, must not be {@code null}
     * @throws IllegalArgumentException if {@code recipe} is {@code null}
     */
    public void setRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("recipe cannot be null");
        }
        this.recipe = recipe;
    }

    /**
     * Returns the ingredient entity for this recipe ingredient.
     *
     * @return the ingredient entity, never {@code null}
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * Sets the ingredient entity for this recipe ingredient.
     *
     * @param ingredient the ingredient entity, must not be {@code null}
     * @throws IllegalArgumentException if {@code ingredient} is {@code null}
     */
    public void setIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("ingredient cannot be null");
        }
        this.ingredient = ingredient;
    }

    /**
     * Returns the amount of this ingredient, if specified.
     *
     * @return the numeric amount, or {@code null} if only an informal amount is used
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Sets the numeric amount of this ingredient.
     *
     * @param amount the amount to set; may be {@code null} if an informal amount is used
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void setAmount(Double amount) {
        if (amount != null && amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.amount = amount;
    }

    /**
     * Returns the measurement unit for the numeric amount.
     *
     * @return the unit string, or {@code null} if not specified
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the measurement unit for the numeric amount.
     *
     * @param unit the unit string, or {@code null} if not specified
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Returns the informal amount description such as "a pinch", if used.
     *
     * @return the informal amount, or {@code null} if a formal amount is used instead
     */
    public String getInformalAmount() {
        return informalAmount;
    }

    /**
     * Sets the informal amount description for this ingredient.
     *
     * @param informalAmount the informal amount text, or {@code null} if not used
     */
    public void setInformalAmount(String informalAmount) {
        this.informalAmount = informalAmount;
    }

    /**
     * Returns the zero-based index of this ingredient within the recipe.
     *
     * @return the position of this ingredient
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the zero-based index of this ingredient within the recipe.
     *
     * @param position the new position, must be non-negative
     * @throws IllegalArgumentException if {@code position} is negative
     */
    public void setPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        this.position = position;
    }

    /**
     * Returns the optional note associated with this ingredient.
     *
     * @return the note text, or {@code null} if not set
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets an optional note for this ingredient.
     *
     * @param note the note text, or {@code null} if no note is needed
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Compares this recipe ingredient to another object for equality.
     * Two recipe ingredients are considered equal if they have the same id, amount, unit,
     * informalAmount, position and note.
     *
     * @param o the object to compare with
     * @return {@code true} if the given object is a
     * RecipeIngredient with the same state; {@code false} otherwise
     */
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

    /**
     * Returns a hash code value for this recipe
     * ingredient, consistent with {@link #equals(Object)}.
     *
     * @return the hash code of this recipe ingredient
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, amount, unit, informalAmount, position, note);
    }
}