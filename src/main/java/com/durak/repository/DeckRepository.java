package com.durak.repository;

import com.durak.entity.Deck;
import org.springframework.data.repository.CrudRepository;

public interface DeckRepository extends CrudRepository<Deck,Long> {
}
