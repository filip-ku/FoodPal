package commons;

import jakarta.persistence.*;

@Entity
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // foreign key -> recipe.id
    @Column(nullable = false)
    private Long recipeId;

    // order in the step list
    @Column(nullable = false)
    private int position;

    // text of the instruction
    @Column(nullable = false, length = 2000)
    private String instruction;

    /**
     * Default constructor for JPA and object mapping.
     */
    public RecipeStep() {
        // for object mapping
    }

    //AI-generated javadoc
    /**
     * Creates a new RecipeStep with the given recipe ID, position, and instruction.
     *
     * @param recipeId     the ID of the associated recipe
     * @param position     the order of this step in the recipe
     * @param instruction  the text describing the step
     */
    public RecipeStep(Long recipeId, int position, String instruction) {
        setRecipeId(recipeId);
        setPosition(position);
        setInstruction(instruction);
    }

    /**
     * Returns the database ID of this recipe step.
     *
     * @return the generated ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the ID of the recipe this step belongs to.
     *
     * @return the recipe ID
     */
    public Long getRecipeId() {
        return recipeId;
    }

    //AI-generated javadoc
    /**
     * Sets the recipe ID for this step.
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
     * Returns the position of this step in the recipe.
     *
     * @return the step order index
     */
    public int getPosition() {
        return position;
    }

    //AI-generated javadoc
    /**
     * Sets the position of this step in the recipe.
     *
     * @param position the step order index (must be non-negative)
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
     * @return the instruction string
     */
    public String getInstruction() {
        return instruction;
    }

    //AI-generated javadoc
    /**
     * Sets the instruction text for this step.
     *
     * @param instruction the step description
     * @throws IllegalArgumentException if {@code instruction} is null
     */
    public void setInstruction(String instruction) {
        if (instruction == null) {
            throw new IllegalArgumentException("instruction cannot be null");
        }
        this.instruction = instruction;
    }
}