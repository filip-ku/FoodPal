package commons;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Objects;

/**
 * Represents a single step in a recipe, including its order and instruction text.
 */
@Entity
public class RecipeStep {

    /**
     * Surrogate primary key used to uniquely identify this recipe step.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Reference to the recipe this step belongs to.
     */
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Recipe recipe;

    /**
     * Zero-based index indicating the order of this step within the recipe.
     */
    @Column(nullable = false)
    private int position;

    /**
     * Text description of what should be done in this step.
     */
    @Column(nullable = false, length = 2000)
    private String instruction;

    /**
     * Default constructor for JPA and object mapping frameworks.
     */
    public RecipeStep() {
        // for object mapping
    }

    /**
     * Creates a new recipe step for the given recipe.
     *
     * @param recipe      the recipe this step belongs to, must not be {@code null}
     * @param position    the zero-based index of this step within the recipe; must be non-negative
     * @param instruction the instruction text for this step, must not be {@code null}
     * @throws IllegalArgumentException if {@code recipe} or {@code instruction}
     * is {@code null}, or if {@code position} is negative
     */
    public RecipeStep(Recipe recipe, int position, String instruction) {
        setRecipe(recipe);
        setPosition(position);
        setInstruction(instruction);
    }

    /**
     * Returns the unique identifier of this recipe step.
     *
     * @return the id of this recipe step, or {@code null} if it has not been persisted yet
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the recipe this step belongs to.
     *
     * @return the owning recipe, never {@code null}
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe this step belongs to.
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
     * Returns the zero-based index of this step within the recipe.
     *
     * @return the position of this step
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the zero-based index of this step within the recipe.
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
     * Returns the instruction text for this step.
     *
     * @return the instruction text, never {@code null}
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * Sets the instruction text for this step.
     *
     * @param instruction the instruction text, must not be {@code null}
     * @throws IllegalArgumentException if {@code instruction} is {@code null}
     */
    public void setInstruction(String instruction) {
        if (instruction == null) {
            throw new IllegalArgumentException("instruction cannot be null");
        }
        this.instruction = instruction;
    }

    /**
     * Compares this recipe step to another object for equality.
     * Two recipe steps are considered equal if they have the same id, position, and instruction.
     *
     * @param o the object to compare with
     * @return {@code true} if the given object is
     * a RecipeStep with the same id, position, and instruction; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeStep that = (RecipeStep) o;
        return position == that.position && Objects.equals(id, that.id)
                && Objects.equals(instruction, that.instruction);
    }

    /**
     * Returns a hash code value for this recipe step, consistent with {@link #equals(Object)}.
     *
     * @return the hash code of this recipe step
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, position, instruction);
    }
}