package nl.hu.inno.menu.domain;

import javax.persistence.*;

@Entity
public class DishIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private Dish dish;

    private Long ingredientId;

    protected DishIngredient() {

    }

    public DishIngredient(Dish dish, Long ingredientId) {
        this.dish = dish;
        this.ingredientId = ingredientId;
    }

    public Long getId() {
        return id;
    }

    public Dish getDishId() {
        return dish;
    }

    public Long getIngredientId() {
        return ingredientId;
    }
}