package commons;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a recipe.
 */
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    // total servings
    @Column(precision = 6, scale = 2)
    private long servings;

    // for recipe language filter
    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("position ASC")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("position ASC")
    private List<RecipeStep> steps = new ArrayList<>();

    /**
     * public no-args constructor for object mapping
     */
    public Recipe() {
    }

    /**
     * Creates a recipe with the given title.
     *
     * @param title non‑null title.
     * @throws IllegalArgumentException if {@code title} is null.
     */
    public Recipe(String title) {
        setTitle(title);
    }

    /**
     * Returns the generated id.
     *
     * @return the generated id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the recipe’s title.
     *
     * @return the recipe’s title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets a new title for this recipe.
     *
     * @param title non‑null title.
     * @throws IllegalArgumentException if {@code title} is null.
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Recipe title cannot be null");
        } else {
            this.title = title;
        }
    }

    // AI-generated
    /**
     * Returns the number of servings for this recipe.
     *
     * @return the number of servings.
     */
    public long getServings() {
        return servings;
    }

    // AI-generated
    /**
     * Sets the number of servings for this recipe.
     *
     * @param servings the number of servings.
     */
    public void setServings(long servings) {
        this.servings = servings;
    }

    // AI-generated
    /**
     * Returns the language code for this recipe.
     *
     * @return the language code.
     */
    public String getLanguageCode() {
        return languageCode;
    }

    // AI-generated
    /**
     * Sets the language code for this recipe.
     *
     * @param languageCode the language code.
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    // AI-generated
    /**
     * Returns the list of ingredients for this recipe.
     *
     * @return the ingredients.
     */
    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    // AI-generated
    /**
     * Replaces the ingredients list for this recipe, updating the bidirectional association.
     *
     * @param ingredients the new list of ingredients.
     */
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) {
            for (RecipeIngredient ingredient : ingredients) {
                addIngredient(ingredient);
            }
        }
    }

    // AI-generated
    /**
     * Adds a single ingredient to this recipe and sets its back-reference.
     *
     * @param ingredient the ingredient to add.
     */
    public void addIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    // AI-generated
    /**
     * Removes a single ingredient from this recipe and clears its back-reference.
     *
     * @param ingredient the ingredient to remove.
     */
    public void removeIngredient(RecipeIngredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }

    // AI-generated
    /**
     * Returns the list of steps for this recipe.
     *
     * @return the steps.
     */
    public List<RecipeStep> getSteps() {
        return steps;
    }

    // AI-generated
    /**
     * Replaces the steps list for this recipe, updating the bidirectional association.
     *
     * @param steps the new list of steps.
     */
    public void setSteps(List<RecipeStep> steps) {
        this.steps.clear();
        if (steps != null) {
            for (RecipeStep step : steps) {
                addStep(step);
            }
        }
    }

    // AI-generated
    /**
     * Adds a single step to this recipe and sets its back-reference.
     *
     * @param step the step to add.
     */
    public void addStep(RecipeStep step) {
        steps.add(step);
        step.setRecipe(this);
    }

    // AI-generated
    /**
     * Removes a single step from this recipe and clears its back-reference.
     *
     * @param step the step to remove.
     */
    public void removeStep(RecipeStep step) {
        steps.remove(step);
        step.setRecipe(null);
    }
}
