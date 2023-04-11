package nl.hu.inno.thuusbezorgd;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
