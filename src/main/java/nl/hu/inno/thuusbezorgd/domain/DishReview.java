package nl.hu.inno.thuusbezorgd.domain;

import nl.hu.inno.thuusbezorgd.security.BaseUser;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DishReview extends Review {

    @ManyToOne
    private Dish dish;

    protected DishReview(){}

    public DishReview(Dish dish, ReviewRating rating, BaseUser user) {
        this.dish = dish;
        super.rating = rating;
        super.user = user;
    }

    public Dish getDish() {
        return dish;
    }
}
