package nl.hu.inno.delivery;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
