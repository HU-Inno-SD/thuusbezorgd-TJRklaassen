package nl.hu.inno.thuusbezorgd;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Primary
public class FakeTimeProvider implements TimeProvider{
    private LocalDateTime now;

    public FakeTimeProvider(){
        //We gaan eten bestellen alsof het 1999 is!
        this.now = LocalDateTime.of(1999, 7, 31, 17, 25);
    }

    @Override
    public LocalDateTime now() {
        return now;
    }


    public void setNow(LocalDateTime now) {
        this.now = now;
    }

    public void advanceDay() {
        this.now = this.now.plusDays(1);
    }

    public synchronized void advanceMinute(){
        this.now = this.now.plusMinutes(1);
    }
}
