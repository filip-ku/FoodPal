package commons;

import jakarta.persistence.*;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;

/**
 * TODO
 * Add @Valid in controller and add @NotBlank/@NotNull/@Size/@Positive for Entity classes.
 * For error good handling, map all responses to HTTP not throws.
 */
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

//    @JsonManagedReference
//    @OneToMany(
//            mappedBy = "recipe",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    @OrderBy("position ASC")
//    private List<Ingredient> ingredients = new ArrayList<>();

//    @Column(nullable = false)
//    private List<RecipeStep> steps = new ArrayList<>();

    public Recipe() {}

    public Recipe(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("title cannot be null");
        } else {
            this.title = title;
        }
    }

//    public List<Ingredient> getIngredients() {
//        return ingredients;
//    }
//
//    public void setIngredients(List<Ingredient> ingredients) {
//        if (ingredients != null) {
//            this.ingredients = ingredients;
//        } else {
//            throw new IllegalArgumentException("Ingredients cannot be null");
//        }
//    }

//    public List<RecipeStep> getSteps() {
//        return steps;
//    }
//
//    public void setSteps(List<RecipeStep> steps) {
//        if (steps != null) {
//            this.steps = steps;
//        } else {
//            throw new IllegalArgumentException("Recipe Steps cannot be null");
//        }
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        Recipe recipe = (Recipe) o;
//        return Objects.equals(id, recipe.id) && Objects.equals(title, recipe.title) && Objects.equals(ingredients, recipe.ingredients) && Objects.equals(steps, recipe.steps);
//    }
//
//    /**
//     * TODO add ingredients, steps to method
//     * @return hashcode
//     */
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, title);
//    }
}
