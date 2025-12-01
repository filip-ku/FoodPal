package commons;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // reference to the owning recipe
    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    // order in the step list
    @Column(nullable = false)
    private int position;

    // text of the instruction
    @Column(nullable = false, length = 2000)
    private String instruction;

    public RecipeStep() {
        // for object mapping
    }

    // AI-generated
    public RecipeStep(Recipe recipe, int position, String instruction) {
        setRecipe(recipe);
        setPosition(position);
        setInstruction(instruction);
    }

    public Long getId() {
        return id;
    }

    // AI-generated
    /**
     * Returns the recipe this step belongs to.
     *
     * @return the owning recipe.
     */
    public Recipe getRecipe() {
        return recipe;
    }

    // AI-generated
    /**
     * Sets the recipe this step belongs to.
     *
     * @param recipe the owning recipe, must not be null.
     */
    public void setRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("recipe cannot be null");
        }
        this.recipe = recipe;
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

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        if (instruction == null) {
            throw new IllegalArgumentException("instruction cannot be null");
        }
        this.instruction = instruction;
    }
}