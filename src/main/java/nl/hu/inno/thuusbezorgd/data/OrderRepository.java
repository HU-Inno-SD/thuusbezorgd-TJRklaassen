package nl.hu.inno.thuusbezorgd.data;

import nl.hu.inno.thuusbezorgd.domain.Order;
import nl.hu.inno.thuusbezorgd.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User currentUser);
}
