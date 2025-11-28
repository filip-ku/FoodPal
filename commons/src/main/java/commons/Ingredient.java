package commons;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * JPA entity representing an ingredient.
 */
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Default constructor required by JPA.
     */
    public Ingredient() {
        // For mapping objects.
    }

    /**
     * Creates an ingredient with the given name.
     * @param name non‑null name.
     * @throws IllegalArgumentException if {@code name} is null
     */
    public Ingredient(String name) {
        setName(name);
    }

    /**
     * Returns the generated id.
     * @return the generated id.
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the ingredient’s name.
     * @return the ingredient’s name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this ingredient.
     *
     * @param name non‑null name.
     * @throws IllegalArgumentException if {@code name} is null.
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Ingredient name cannot be null");
        } else {
            this.name = name;
        }
    }
}
