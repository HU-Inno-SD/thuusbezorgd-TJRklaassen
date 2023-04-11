package nl.hu.inno.thuusbezorgd.data;

import nl.hu.inno.thuusbezorgd.domain.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Long> {
}
