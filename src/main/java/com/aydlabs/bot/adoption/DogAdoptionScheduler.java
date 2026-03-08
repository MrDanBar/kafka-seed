package com.aydlabs.bot.adoption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DogAdoptionScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DogAdoptionScheduler.class);

    @Tool(description = "schedule an appointment to pickup or adopt a dog from a AYDLABS NE location")
    public String schedule(final int dogId, final String dogName) {
        System.out.println("Scheduling adoption for dog " + dogName);

        LOGGER.info("Scheduling adoption for dog {}", dogName);

        return Instant.now()
                      .plus(3, ChronoUnit.DAYS)
                      .toString();
    }

    @Tool(description = "email notification from a AYDLABS NE location")
    public String emailNotification(final int dogId, final String dogName) {
        System.out.println("email for dog " + dogName);

        LOGGER.info("email for dog {}", dogName);

        return Instant.now()
                      .plus(3, ChronoUnit.DAYS)
                      .toString();
    }
}
