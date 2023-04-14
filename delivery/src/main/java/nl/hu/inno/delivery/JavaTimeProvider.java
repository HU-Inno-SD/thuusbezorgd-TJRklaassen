package nl.hu.inno.stock;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JavaTimeProvider implements TimeProvider{
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
