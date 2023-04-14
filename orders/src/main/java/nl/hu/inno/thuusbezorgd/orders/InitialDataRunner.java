package nl.hu.inno.thuusbezorgd;

import nl.hu.inno.thuusbezorgd.domain.Rider;
import nl.hu.inno.thuusbezorgd.security.User;
import nl.hu.inno.thuusbezorgd.domain.Dish;
import nl.hu.inno.thuusbezorgd.domain.Ingredient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;

@Component
@Profile("dev")
public class InitialDataRunner implements CommandLineRunner {

    private final EntityManager entities;

    public InitialDataRunner(EntityManager entities) {
        this.entities = entities;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        this.entities.persist(new User("admin", "admin"));
        this.entities.persist(new User("tom", "Tom123"));
        this.entities.persist(new User("mirko", "Mirko456"));
        this.entities.persist(new User("robin", "0fir%%cQJ|Rc!!=&fIKsRI"));

        Ingredient broodje = new Ingredient("Bun", true);
        Ingredient burger = new Ingredient("Burger", false);
        Ingredient vegaburger = new Ingredient("Vegaburger", true);
        Ingredient kaas = new Ingredient("Cheese", false);
        Ingredient sla = new Ingredient("Lettuce", true);
        Ingredient tomaat = new Ingredient("Tomato", true);

        for (Ingredient i : Arrays.asList(
                broodje, burger, vegaburger, kaas, sla, tomaat
        )) {
            i.deliver(10 * 1000);
            entities.persist(i);
        }

        for (Dish d : Arrays.asList(
                new Dish("Burger", broodje, burger, kaas, sla, tomaat),
                new Dish("Vegaburger", broodje, vegaburger, sla, tomaat),
                new Dish("Salad", sla, tomaat),
                new Dish("Croque Monsieur", broodje, kaas)
        )) {
            entities.persist(d);
        }

        Rider wynona = new Rider("Wynona");
        entities.persist(wynona);
    }
}
