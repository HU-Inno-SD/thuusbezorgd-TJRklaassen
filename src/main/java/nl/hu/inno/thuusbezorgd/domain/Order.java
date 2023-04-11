package nl.hu.inno.thuusbezorgd.domain;

import nl.hu.inno.thuusbezorgd.security.BaseUser;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders") //Order is een keyword in sql, so this works around some wonky sql-generator implementations
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private BaseUser user;

    private LocalDateTime orderDate;

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    @OneToOne
    private Delivery delivery;

    private Address address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    @OneToMany(mappedBy = "id.order")
    @Cascade(CascadeType.PERSIST)
    private List<OrderedDish> orderedDishes;

    protected Order() {

    }

    public Order(BaseUser u, Address address) {
        this.user = u;
        this.orderedDishes = new ArrayList<>();
        this.address = address;
        this.status = OrderStatus.Received;
    }

    public Long getId() {
        return id;
    }

    public BaseUser getUser() {
        return user;
    }

    public List<Dish> getOrderedDishes() {
        return this.orderedDishes.stream().map(OrderedDish::getDish).toList();
    }

    public List<Long> getOrderedDishIds() {
        List<Long> ordered = new ArrayList<>();

        for (OrderedDish od : this.orderedDishes) {
            ordered.add(od.getDishId());
        }

        return Collections.unmodifiableList(ordered);
    }

    public void addDish(Dish dish) {
        this.orderedDishes.add(new OrderedDish(this, dish));
    }

    public Address getAddress() {
        return address;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void process(LocalDateTime orderMoment) {
        this.orderDate = orderMoment;
        for (Dish d : this.getOrderedDishes()) {
            d.prepare();
        }
    }
}
