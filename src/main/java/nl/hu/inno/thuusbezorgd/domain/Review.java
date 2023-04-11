package nl.hu.inno.thuusbezorgd.domain;

import nl.hu.inno.thuusbezorgd.security.BaseUser;

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
    protected BaseUser user;

    protected Review(){}

    public long getId() {
        return id;
    }

    public ReviewRating getRating() {
        return rating;
    }

    public BaseUser getUser() {
        return user;
    }
}
