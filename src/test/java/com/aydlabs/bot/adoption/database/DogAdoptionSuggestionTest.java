package com.aydlabs.bot.adoption.database;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DogAdoptionSuggestionTest {

    @Test
    void suggestion_recordAccessors() {
        final DogAdoptionSuggestion suggestion = new DogAdoptionSuggestion(1, "Rex", "A friendly dog");

        assertThat(suggestion.id()).isEqualTo(1);
        assertThat(suggestion.name()).isEqualTo("Rex");
        assertThat(suggestion.description()).isEqualTo("A friendly dog");
    }

    @Test
    void suggestion_equalityAndHashCode() {
        final DogAdoptionSuggestion s1 = new DogAdoptionSuggestion(1, "Rex", "A friendly dog");
        final DogAdoptionSuggestion s2 = new DogAdoptionSuggestion(1, "Rex", "A friendly dog");

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
    }
}
