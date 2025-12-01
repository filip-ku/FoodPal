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

    public RecipeIngredient() {
        // for object mapping
    }

    public RecipeIngredient(Long recipeId, Long ingredientId, int position) {
        this.setRecipeId(recipeId);
        this.setIngredientId(ingredientId);
        this.setPosition(position);
    }

    public Long getId() {
        return id;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        if (recipeId == null) {
            throw new IllegalArgumentException("recipeId cannot be null");
        }
        this.recipeId = recipeId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        if (ingredientId == null) {
            throw new IllegalArgumentException("ingredientId cannot be null");
        }
        this.ingredientId = ingredientId;
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
}