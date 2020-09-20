package com.durak.service.Class;

import com.durak.entity.Deck;
import com.durak.repository.DeckRepository;
import com.durak.service.Interface.DeckDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeckImplService implements DeckDAO {
    @Autowired
    private DeckRepository deckRepository;

    @Override
    public void save(Deck deck) {
        deckRepository.save(deck);
    }
}
