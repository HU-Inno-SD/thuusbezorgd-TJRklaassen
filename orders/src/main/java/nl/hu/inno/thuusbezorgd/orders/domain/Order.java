package nl.hu.inno.thuusbezorgd.orders.domain;

import nl.hu.inno.thuusbezorgd.orders.security.User;
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
    private User user;

    private LocalDateTime orderDate;

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    private Address address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    @OneToMany(mappedBy = "order")
    @Cascade(CascadeType.PERSIST)
    private List<OrderedDish> orderedDishes;

    protected Order() {

    }

    public Order(User u, Address address) {
        this.user = u;
        this.orderedDishes = new ArrayList<>();
        this.address = address;
        this.status = OrderStatus.Received;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<OrderedDish> getOrderedDishes() {
        return Collections.unmodifiableList(this.orderedDishes);
    }

    public void addDish(Long dishId) {
        this.orderedDishes.add(new OrderedDish(this, dishId));
    }

    public Address getAddress() {
        return address;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void process(LocalDateTime orderMoment) {
        this.orderDate = orderMoment;
    }
}
