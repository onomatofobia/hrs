package pl.com.bottega.hrs.model;

import java.time.Clock;
import java.time.LocalDate;

public class StandardTimeProvider implements TimeProvider {
    @Override
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
