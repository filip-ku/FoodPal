package commons;

import jakarta.persistence.*;

@Entity
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // foreign key -> recipe.id
    @Column(nullable = false)
    private Long recipeId;

    // foreign key -> ingredient.id
    @Column(nullable = false)
    private Long ingredientId;

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
     * Default constructor for JPA and object mapping.
     */
    public RecipeIngredient() {
        // for object mapping
    }

    /**
     * Creates a new RecipeIngredient with required fields.
     *
     * @param recipeId     the ID of the related recipe
     * @param ingredientId the ID of the related ingredient
     * @param position     the ingredient's order in the recipe
     */
    public RecipeIngredient(Long recipeId, Long ingredientId, int position) {
        this.setRecipeId(recipeId);
        this.setIngredientId(ingredientId);
        this.setPosition(position);
    }

    /**
     * Returns the database ID of this RecipeIngredient.
     *
     * @return the generated ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the ID of the associated recipe.
     *
     * @return the recipe ID
     */
    public Long getRecipeId() {
        return recipeId;
    }

    //AI-generated javadoc
    /**
     * Sets the recipe ID for this ingredient.
     *
     * @param recipeId the recipe ID to assign
     * @throws IllegalArgumentException if {@code recipeId} is null
     */
    public void setRecipeId(Long recipeId) {
        if (recipeId == null) {
            throw new IllegalArgumentException("recipeId cannot be null");
        }
        this.recipeId = recipeId;
    }

    /**
     * Returns the ID of the associated ingredient.
     *
     * @return the ingredient ID
     */
    public Long getIngredientId() {
        return ingredientId;
    }

    //AI-generated javadoc
    /**
     * Sets the ingredient ID for this entry.
     *
     * @param ingredientId the ingredient ID to assign
     * @throws IllegalArgumentException if {@code ingredientId} is null
     */
    public void setIngredientId(Long ingredientId) {
        if (ingredientId == null) {
            throw new IllegalArgumentException("ingredientId cannot be null");
        }
        this.ingredientId = ingredientId;
    }

    //AI-generated javadoc
    /**
     * Returns the quantity amount of this ingredient.
     *
     * @return the amount, or {@code null} if informal
     */
    public Double getAmount() {
        return amount;
    }

    //AI-generated javadoc
    /**
     * Sets the ingredient amount.
     *
     * @param amount the quantity value; may be {@code null} for informal entries
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void setAmount(Double amount) {
        if (amount != null && amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.amount = amount;
    }

    //AI-generated javadoc
    /**
     * Returns the unit for the ingredient amount.
     *
     * @return the unit, or {@code null} if informal
     */
    public String getUnit() {
        return unit;
    }

    //AI-generated javadoc
    /**
     * Sets the unit for this ingredient.
     *
     * @param unit the unit string; may be {@code null} if informal
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    //AI-generated javadoc
    /**
     * Returns the informal amount (e.g. "a pinch").
     *
     * @return the informal amount, or {@code null} if a formal amount is used
     */
    public String getInformalAmount() {
        return informalAmount;
    }

    //AI-generated javadoc
    /**
     * Sets the informal amount.
     *
     * @param informalAmount a descriptive quantity (e.g. "a pinch"), or {@code null}
     */
    public void setInformalAmount(String informalAmount) {
        this.informalAmount = informalAmount;
    }

    /**
     * Returns the ingredient's order in the recipe list.
     *
     * @return the position index
     */
    public int getPosition() {
        return position;
    }

    //AI-generated javadoc
    /**
     * Sets the ingredient's order in the recipe list.
     *
     * @param position the position index (must be non-negative)
     * @throws IllegalArgumentException if {@code position} is negative
     */
    public void setPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        this.position = position;
    }

    //AI-generated javadoc
    /**
     * Returns the optional note for this ingredient.
     *
     * @return the note text, or {@code null} if not set
     */
    public String getNote() {
        return note;
    }

    //AI-generated javadoc
    /**
     * Sets an optional note for this ingredient.
     *
     * @param note additional text note; may be {@code null}
     */
    public void setNote(String note) {
        this.note = note;
    }
}