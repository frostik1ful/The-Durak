package com.durak.viewData;

import com.durak.viewData.CardData.CardData;

import java.util.List;

public class CardPackage {
    private int cardsLeft;
    List<CardData> newCards;

    public CardPackage(int cardsLeft, List<CardData> newCards) {
        this.cardsLeft = cardsLeft;
        this.newCards = newCards;
    }

    public int getCardsLeft() {
        return cardsLeft;
    }

    public List<CardData> getNewCards() {
        return newCards;
    }
}
