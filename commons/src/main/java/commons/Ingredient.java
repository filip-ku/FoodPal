package commons;

//import jakarta.persistence.*;
//
//import java.util.Objects;

//@Entity
public class Ingredient {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 200, unique = true)
//    private String name;
//
//    @Column(nullable = false)
//    private int protein;
//
//    @Column(nullable = false)
//    private int fat;
//
//    @Column(nullable = false)
//    private int carbs;
//
//    /**
//     * Empty constructor for dependency injection
//     */
//    public Ingredient() {}
//
//    public Ingredient(String name, int protein, int fat, int carbs) {
//        this.name = name;
//        this.protein = protein;
//        this.fat = fat;
//        this.carbs = carbs;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        if (name == null) {
//            throw new IllegalArgumentException("name cannot be null");
//        } else {
//            this.name = name;
//        }
//    }
//
//    public int getProtein() {
//        return protein;
//    }
//
//    public void setProtein(int protein) {
//        this.protein = protein;
//    }
//
//    public int getFat() {
//        return fat;
//    }
//
//    public void setFat(int fat) {
//        this.fat = fat;
//    }
//
//    public int getCarbs() {
//        return carbs;
//    }
//
//    public void setCarbs(int carbs) {
//        this.carbs = carbs;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        Ingredient that = (Ingredient) o;
//        return protein == that.protein && fat == that.fat && carbs == that.carbs && Objects.equals(id, that.id) && Objects.equals(name, that.name);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, protein, fat, carbs);
//    }
}
