package commons;

import jakarta.persistence.*;

@Entity
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // foreign key → recipe.id
    @Column(nullable = false)
    private Long recipeId;

    // order in the step list
    @Column(nullable = false)
    private int position;

    // text of the instruction
    @Column(nullable = false, length = 2000)
    private String instruction;

    public RecipeStep() {
        // for object mapping
    }

    public RecipeStep(Long recipeId, int position, String instruction) {
        setRecipeId(recipeId);
        setPosition(position);
        setInstruction(instruction);
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