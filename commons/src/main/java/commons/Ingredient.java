package commons;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * JPA entity representing an ingredient.
 */
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    private String name;

    /** Protein content per 100 grams (may be null if unknown). */
    @Column()
    private Double proteinPer100g;


    /** Fat content per 100 grams (may be null if unknown). */
    @Column()
    private Double fatPer100g;


    /** Carbohydrate content per 100 grams (may be null if unknown). */
    @Column()
    private Double carbsPer100g;

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
    public Long getId() {
        return id;
    }

    /**
     * Returns the ingredient’s name.
     * @return the ingredient’s name.
     */
    public String getName() {
        return name;
    }

    // AI-generated
    /**
     * Returns the protein content per 100 grams.
     *
     * @return protein per 100 grams, or {@code null} if unknown.
     */
    public Double getProteinPer100g() {
        return proteinPer100g;
    }

    // AI-generated
    /**
     * Sets the protein content per 100 grams.
     *
     * @param proteinPer100g protein per 100 grams, may be {@code null} if unknown.
     */
    public void setProteinPer100g(Double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    // AI-generated
    /**
     * Returns the fat content per 100 grams.
     *
     * @return fat per 100 grams, or {@code null} if unknown.
     */
    public Double getFatPer100g() {
        return fatPer100g;
    }

    // AI-generated
    /**
     * Sets the fat content per 100 grams.
     *
     * @param fatPer100g fat per 100 grams, may be {@code null} if unknown.
     */
    public void setFatPer100g(Double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    // AI-generated
    /**
     * Returns the carbohydrate content per 100 grams.
     *
     * @return carbs per 100 grams, or {@code null} if unknown.
     */
    public Double getCarbsPer100g() {
        return carbsPer100g;
    }

    // AI-generated
    /**
     * Sets the carbohydrate content per 100 grams.
     *
     * @param carbsPer100g carbs per 100 grams, may be {@code null} if unknown.
     */
    public void setCarbsPer100g(Double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
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

    /**
     * equals method
     * @param o   the reference object with which to compare.
     * @return true is equals, else false
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(proteinPer100g, that.proteinPer100g)
                && Objects.equals(fatPer100g, that.fatPer100g)
                && Objects.equals(carbsPer100g, that.carbsPer100g);
    }

    /**
     * hashcode for an ingredient
     * @return hashcode for the ingredient
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, proteinPer100g, fatPer100g, carbsPer100g);
    }
}
