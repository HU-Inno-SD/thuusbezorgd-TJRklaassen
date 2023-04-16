package nl.hu.inno.delivery.domain;

import nl.hu.inno.delivery.security.User;

import javax.persistence.*;

@Entity
@Inheritance
public abstract class Review {

    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.STRING)
    protected ReviewRating rating;

    @ManyToOne
    protected User user;

    protected Review(){}

    public long getId() {
        return id;
    }

    public ReviewRating getRating() {
        return rating;
    }

    public User getUser() {
        return user;
    }
}
