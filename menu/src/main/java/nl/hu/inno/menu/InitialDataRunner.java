package nl.hu.inno.menu;

import nl.hu.inno.menu.domain.Dish;
import nl.hu.inno.menu.domain.DishIngredient;
import nl.hu.inno.menu.security.User;
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

        Dish d1 = new Dish("Hamburger");
        DishIngredient d1i1 = new DishIngredient(d1, 1L);
        DishIngredient d1i2 = new DishIngredient(d1, 2L);
        DishIngredient d1i3 = new DishIngredient(d1, 4L);
        DishIngredient d1i4 = new DishIngredient(d1, 5L);
        DishIngredient d1i5 = new DishIngredient(d1, 6L);
        d1.addIngredients(d1i1, d1i2, d1i3, d1i4, d1i5);

        Dish d2 = new Dish("Vegaburger");
        DishIngredient d2i1 = new DishIngredient(d2, 1L);
        DishIngredient d2i2 = new DishIngredient(d2, 3L);
        DishIngredient d2i3 = new DishIngredient(d1, 5L);
        DishIngredient d2i4 = new DishIngredient(d1, 6L);
        d2.addIngredients(d2i1, d2i2, d2i3, d2i4);

        entities.persist(d1);
        entities.persist(d2);
    }
}
