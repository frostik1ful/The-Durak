package com.durak.service.Interface;

import com.durak.entity.Card;

import java.util.Optional;

public interface CardDAO {
    Optional<Card> findByIdAndPlayerId(long id, long playerId);
}
