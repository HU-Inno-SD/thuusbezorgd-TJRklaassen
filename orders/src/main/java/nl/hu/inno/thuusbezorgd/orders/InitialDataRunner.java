package nl.hu.inno.thuusbezorgd.orders;

import nl.hu.inno.thuusbezorgd.orders.security.User;
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
    }
}
