package nl.hu.inno.delivery.domain;


import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@NamedNativeQuery(name = "Delivery.findRandom", resultClass = Delivery.class,
        query = "select * from delivery order by random() limit 1")
public class Delivery {
    @Id
    @GeneratedValue
    private Long id;

    private boolean completed;

    @ManyToOne
    private Rider rider;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
    private DeliveryOrder deliveryOrder;

    public Long getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public DeliveryOrder getDeliveryOrder() {
        return deliveryOrder;
    }

    protected Delivery(){}

    public Delivery(DeliveryOrder deliveryOrder, Rider rider){
        this.deliveryOrder = deliveryOrder;
        this.rider = rider;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted(){
        this.completed = true;
        // TODO: Message to order to mark status
//        this.deliveryOrder.setStatus(OrderStatus.Delivered);
    }
}
