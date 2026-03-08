package com.aydlabs.bot.adoption.database;

import org.springframework.data.repository.ListCrudRepository;

public interface DogRepository extends ListCrudRepository<Dog, Integer> {
}
