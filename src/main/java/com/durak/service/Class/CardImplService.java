package com.durak.service.Class;

import com.durak.entity.Card;
import com.durak.repository.CardRepository;
import com.durak.service.Interface.CardDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardImplService implements CardDAO {
    @Autowired
    private CardRepository cardRepository;

    @Override
    public Optional<Card> findByIdAndPlayerId(long id, long playerId) {
        return cardRepository.findByIdAndPlayerId(id, playerId);
    }
}
