package commons;

import jakarta.persistence.*;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 200)
    private String name;

    public Ingredient() {
        // For mapping objects.
    }

    public Ingredient(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Ingredient name cannot be null");
        } else {
            this.name = name;
        }
    }
}
