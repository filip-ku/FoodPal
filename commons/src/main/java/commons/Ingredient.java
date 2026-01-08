package commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Objects;

/**
 * JPA entity representing an ingredient.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
     * constructor for ingredient
     * @param name  name of the ingredient
     * @param proteinPer100g protein per 100g of ingredient
     * @param fatPer100g  fat per 100g of ingredient
     * @param carbsPer100g  carbs per 100g of ingredient
     */
    public Ingredient(String name, Double proteinPer100g, Double fatPer100g, Double carbsPer100g) {
        setName(name);
        setProteinPer100g(proteinPer100g);
        setFatPer100g(fatPer100g);
        setCarbsPer100g(carbsPer100g);
    }

    /**
     * constructor for tests
     * @param name name of an ingredient
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
     * Sets a new id for this ingredient.
     * This method should only be used for testing.
     * Normally the ingredient id will be set by the server.
     *
     * @param id new id that will be set by this method
     */
    public void setId(Long id) {this.id = id;}

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
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Ingredient name cannot be null");
        } else {
            this.name = name;
        }
    }

    /**
     * Returns the calorie amount per 100 grams.
     *
     * @return calorie per 100 grams, or {@code null} if unknown.
     */
    public Double getCalories() {
        return proteinPer100g * 4.0 + carbsPer100g * 4.0 + fatPer100g * 9.0;
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

    /**
     * Utility to check if a string is null or empty
     * @param s the string to check
     * @return true if is null or empty, else false
     */
    private static boolean isNullOrEmpty(String s){
        return s == null || s.isEmpty();
    }
}
