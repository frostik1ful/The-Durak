package com.durak.util;

import com.durak.entity.Card;
import com.durak.viewData.CardData.CardData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardPathCreator {
    private final String paramName = "background-image:";
    private final String pathStart = "url('/images/cards";
    private final String pathEnd = ".png')";

    public List<CardData> getCardsData(List<Card> cards) {
        List<CardData> paths = new ArrayList<>();
        cards.forEach(card -> paths.add(new CardData(card.getId(), getSinglePath(card))));
        return paths;
    }

    public CardData getSingleCardData(Card card) {
        return new CardData(card.getId(), getSinglePath(card));
    }

    public String getSinglePath(Card card) {
        return paramName + pathStart + getSimpleSinglePath(card);
    }

    public String getAbsoluteSingleCardPath(Card card) {
        return pathStart + getSimpleSinglePath(card);
    }

    private String getSimpleSinglePath(Card card) {
        return "/" + card.getSuit().toString().toLowerCase() + "/" + card.getValue().toString().toLowerCase() + pathEnd;
    }
}
