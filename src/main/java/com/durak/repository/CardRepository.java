package com.durak.repository;

import com.durak.entity.Card;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CardRepository extends CrudRepository<Card,Long> {

    public Optional<Card> findByIdAndPlayerId(long id, long playerId);
}
