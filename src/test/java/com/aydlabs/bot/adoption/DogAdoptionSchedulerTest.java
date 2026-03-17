package com.aydlabs.bot.adoption;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class DogAdoptionSchedulerTest {

    private final DogAdoptionScheduler scheduler = new DogAdoptionScheduler();

    @Test
    void schedule_returnsDateThreeDaysFromNow() {
        final Instant before = Instant.now().plus(3, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS);
        final String result = scheduler.schedule(1, "Rex");
        final Instant after = Instant.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS);

        final Instant parsed = Instant.parse(result);
        assertThat(parsed).isAfter(before).isBefore(after);
    }

    @Test
    void emailNotification_returnsDateThreeDaysFromNow() {
        final Instant before = Instant.now().plus(3, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS);
        final String result = scheduler.emailNotification(1, "Rex");
        final Instant after = Instant.now().plus(3, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS);

        final Instant parsed = Instant.parse(result);
        assertThat(parsed).isAfter(before).isBefore(after);
    }
}
