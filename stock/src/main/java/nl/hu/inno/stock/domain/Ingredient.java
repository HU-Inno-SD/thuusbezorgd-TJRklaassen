package nl.hu.inno.thuusbezorgd.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private boolean vegetarian;
    private int nrInStock;

    protected Ingredient() {
        //For Hibernate
    }

    public Ingredient(String name, boolean vegetarian) {
        this.name = name;
        this.vegetarian = vegetarian;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return vegetarian == that.vegetarian && Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return this.id;
    }

    public int getNrInStock() {
        return nrInStock;
    }

    public void take(int n){
        if(n > this.nrInStock){
            throw new RuntimeException("Out of stock: " + this.getName());
        }
        this.nrInStock -= n;
    }

    public void deliver(int n){
        this.nrInStock += n;
    }
}
