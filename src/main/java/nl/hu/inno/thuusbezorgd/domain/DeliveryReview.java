package nl.hu.inno.thuusbezorgd.domain;

import nl.hu.inno.thuusbezorgd.security.BaseUser;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DeliveryReview extends Review {

    @ManyToOne
    private Delivery delivery;


    protected DeliveryReview() {
    }

    public DeliveryReview(Delivery delivery, ReviewRating rating, BaseUser user) {
        this.delivery = delivery;
        super.rating = rating;
        super.user = user;
    }

    public Delivery getDelivery() {
        return delivery;
    }
}
