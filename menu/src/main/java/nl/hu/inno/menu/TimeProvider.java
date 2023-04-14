package nl.hu.inno.stock;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
