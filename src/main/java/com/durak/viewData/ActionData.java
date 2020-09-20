package com.durak.viewData;

import com.durak.viewData.CardData.CardData;

import java.util.LinkedList;
import java.util.List;

public class ActionData {
    private String actionButtonText;
    private List<CardData> cardsToTakeFromField = new LinkedList<>();
    private List<CardData> cardsToTakeFromDeck = new LinkedList<>();
    private int cardsLeftInDeck;
    public ActionData() {
    }

    public ActionData(String actionButtonText, List<CardData> cardsToTakeFromField, List<CardData> cardsToTakeFromDeck, int cardsLeftInDeck) {
        this.actionButtonText = actionButtonText;
        this.cardsToTakeFromField = cardsToTakeFromField;
        this.cardsToTakeFromDeck = cardsToTakeFromDeck;
        this.cardsLeftInDeck = cardsLeftInDeck;
    }

    public String getActionButtonText() {
        return actionButtonText;
    }

    public void setActionButtonText(String actionButtonText) {
        this.actionButtonText = actionButtonText;
    }

    public List<CardData> getCardsToTakeFromField() {
        return cardsToTakeFromField;
    }

    public void setCardsToTakeFromField(List<CardData> cardsToTakeFromField) {
        this.cardsToTakeFromField = cardsToTakeFromField;
    }

    public List<CardData> getCardsToTakeFromDeck() {
        return cardsToTakeFromDeck;
    }

    public void setCardsToTakeFromDeck(List<CardData> cardsToTakeFromDeck) {
        this.cardsToTakeFromDeck = cardsToTakeFromDeck;
    }

    public int getCardsLeftInDeck() {
        return cardsLeftInDeck;
    }

    public void setCardsLeftInDeck(int cardsLeftInDeck) {
        this.cardsLeftInDeck = cardsLeftInDeck;
    }
}
