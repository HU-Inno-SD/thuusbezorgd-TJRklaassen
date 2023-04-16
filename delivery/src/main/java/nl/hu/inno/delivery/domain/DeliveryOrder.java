package nl.hu.inno.delivery.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class DeliveryOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @JoinColumn(name = "delivery_id")
    private List<Delivery> deliveries;

    private Long orderId;

    protected DeliveryOrder() {

    }

    public DeliveryOrder(Long orderId) {
        this.orderId = orderId;
    }

    public Long getId() {
        return id;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void addDelivery(Delivery delivery) {
        if(deliveries == null) {
            deliveries = new ArrayList<>();
        }

        deliveries.add(delivery);
    }

    public Long getOrderId() {
        return orderId;
    }
}
