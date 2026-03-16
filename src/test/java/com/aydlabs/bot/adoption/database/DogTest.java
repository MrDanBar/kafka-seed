package com.aydlabs.bot.adoption.database;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DogTest {

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
}
