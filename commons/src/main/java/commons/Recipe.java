package commons;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

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
    @Column()
    private BigDecimal servings;

    /**
     * Language code for the recipe (e.g., "en", "nl", "es").
     * Represents the language of the ingredients and preparation instructions.
     */
    @Column(length = 10)
    private String language;

    /**
     * Local-only flag indicating if this recipe is marked as favorite by the current user.
     * This field is not persisted to the database and is managed client-side only.
     */
    @Transient
    private boolean favorite = false;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<RecipeIngredient> ingredients = new HashSet<>();

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
     * @param servings amount of servings of the recipe
     * @throws IllegalArgumentException if {@code title} is null.
     */
    public Recipe(String title, BigDecimal servings) {
        setTitle(title);
        setServings(servings);
    }

    /**
     * constructor for tests in server
     * @param title title of a recipe
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
     * Sets a new id for this recipe.
     * This method should only be used for testing.
     * Normally the recipe id will be set by the server.
     *
     * @param id new id that will be set by this method
     */
    public void setId(Long id) {this.id = id;}

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
    public BigDecimal getServings() {
        return servings;
    }

    // AI-generated
    /**
     * Sets the number of servings for this recipe.
     *
     * @param servings the number of servings.
     */
    public void setServings(BigDecimal servings) {
        this.servings = servings;
    }

    /**
     * Returns the language code for this recipe.
     *
     * @return the language code (e.g., "en", "nl", "es").
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language code for this recipe.
     *
     * @param language the language code (e.g., "en", "nl", "es").
     */
    public void setLanguage(String language) {
        this.language = language;
    }


    /**
     * Returns whether this recipe is marked as favorite.
     *AI generated
     * @return true if favorite, false otherwise
     */
    public boolean isFavorite() {return favorite;}

    /**
     * Sets the favorite status of this recipe.
     * AI generated
     * @param favorite true to mark as favorite, false otherwise
     */
    public void setFavorite(boolean favorite) {this.favorite = favorite;}

    // AI-generated
    /**
     * Returns the list of ingredients for this recipe.
     *
     * @return the ingredients.
     */
    public Set<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    // AI-generated
    /**
     * Replaces the ingredients list for this recipe, updating the bidirectional association.
     *
     * @param ingredients the new list of ingredients.
     */
    public void setIngredients(Set<RecipeIngredient> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) {
            for (RecipeIngredient ingredient : ingredients) {
                addRecipeIngredient(ingredient);
            }
        }
    }

    // AI-generated
    /**
     * Adds a single ingredient to this recipe and sets its back-reference.
     *
     * @param ingredient the ingredient to add.
     */
    public void addRecipeIngredient(RecipeIngredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    // AI-generated
    /**
     * Removes a single ingredient from this recipe and clears its back-reference.
     *
     * @param recipeIngredient the ingredient to remove.
     */
    public void removeIngredient(RecipeIngredient recipeIngredient) {
        ingredients.remove(recipeIngredient);
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
    }

    /**
     * equals method
     * @param o   the reference object with which to compare.
     * @return true is equals, else false
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(servings, recipe.servings)
                && Objects.equals(id, recipe.id)
                && Objects.equals(title, recipe.title)
                && Objects.equals(language, recipe.language);
    }

    /**
     * hashcode for recipe
     * @return hashcode of the recipe
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, servings, language);
    }
}
