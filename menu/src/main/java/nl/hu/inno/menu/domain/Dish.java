package nl.hu.inno.menu.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.inno.menu.messaging.Messenger;

import javax.persistence.*;
import java.util.*;

@Entity
public class Dish {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @OneToMany(mappedBy = "dish", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<DishIngredient> dishIngredients;

    protected Dish() {
        //For Hibernate
    }

    public Dish(String name) {
        this.name = name;
        this.dishIngredients = new ArrayList<DishIngredient>();
    }

    public Dish(String name, DishIngredient... dishIngredients) {
        this.name = name;
        this.dishIngredients = Arrays.asList(dishIngredients);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addIngredients(DishIngredient... ingredients) {
        dishIngredients.addAll(Arrays.asList(ingredients));
    }

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public boolean isVegetarian() {
        // TODO: Bij genoeg tijd dit nog via messaging doen
//        return this.ingredients.stream().allMatch(Ingredient::isVegetarian);
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

//    public int getAvailable() {
//        // TODO: Dit via messaging doen
//
//
////        return this.getIngredients().stream().mapToInt(Ingredient::getNrInStock).min().orElse(0);
//        return 6;
//    }
//
//    public void prepare(){
//        // TODO: Via messaging doen
////        for(Ingredient i: this.ingredients){
////            i.take(1);
////        }
//    }
}
