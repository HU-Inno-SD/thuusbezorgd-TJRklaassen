package nl.hu.inno.thuusbezorgd.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Rider {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Rider(String name) {
        this.name = name;
    }

    protected Rider() {

    }

    @OneToMany(mappedBy = "rider")
    private List<Delivery> deliveries = new ArrayList<>();

    public int getNrOfDeliveries() {
        return deliveries.size();
    }

    public String getName() {
        return this.name;
    }
}
