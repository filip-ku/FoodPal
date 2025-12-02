package commons;

import jakarta.persistence.*;

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

    /**
     * public no-args constructor for object mapping
     */
    public Recipe() {}

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
}
