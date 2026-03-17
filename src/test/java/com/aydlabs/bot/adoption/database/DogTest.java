package com.aydlabs.bot.adoption.database;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DogTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DogTest.class);

    @Test
    void dog_recordAccessors() {
        final Dog dog = new Dog(1, "Rex", "owner", "A friendly dog");

        assertThat(dog.id()).isEqualTo(1);
        assertThat(dog.name()).isEqualTo("Rex");
        assertThat(dog.owner()).isEqualTo("owner");
        assertThat(dog.description()).isEqualTo("A friendly dog");
    }

    @Test
    void dog_equalityAndHashCode() {
        final Dog dog1 = new Dog(1, "Rex", "owner", "A friendly dog");
        final Dog dog2 = new Dog(1, "Rex", "owner", "A friendly dog");

        assertThat(dog1).isEqualTo(dog2);
        assertThat(dog1.hashCode()).isEqualTo(dog2.hashCode());
    }

    @Test
    void test() {
        final var bills = List.of(100, 50, 20, 10, 5, 1);

        final var input = List.of(300, 87, 99);
        final var output = new ArrayList<>();

        for (var value : input) {
            int nominalValue = value;
            int numberOfBills = 0;

            while (nominalValue > 0) {
                for (var bill : bills) {
                    if (nominalValue >= bill && nominalValue - bill >= 0) {
                        nominalValue -= bill;
                        numberOfBills++;
                        break;
                    }
                }

                LOGGER.info("{} -> [{}] nominal -> {}", value, numberOfBills, nominalValue);
            }

            output.add(numberOfBills);
        }

        LOGGER.info("output: {}", output);
    }
}
