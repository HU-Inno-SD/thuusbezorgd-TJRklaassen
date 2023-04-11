package nl.hu.inno.thuusbezorgd.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class OrderedDish {
    @Embeddable
    public static class OrderedDishId implements Serializable {
        @ManyToOne
        private Dish dish;

        @ManyToOne
        private Order order;
    }

    @EmbeddedId
    private OrderedDishId id;

    protected OrderedDish() {

    }

    public OrderedDish(Order owner, Dish ordered) {
        this.id = new OrderedDishId();
        this.id.dish = ordered;
        this.id.order = owner;
        this.nr = 1;
    }

    private int nr;

    public int getNr() {
        return nr;
    }

    public Order getOrder() {
        return this.id.order;
    }

    public Long getDishId() {
        return this.id.dish.getId();
    }

    public Dish getDish() {
        return this.id.dish;
    }

}
