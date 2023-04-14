package nl.hu.inno.thuusbezorgd.orders;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
