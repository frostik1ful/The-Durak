package com.durak.viewData;

import com.durak.viewData.CardData.CardData;
import com.durak.viewData.CardData.EnemyHandCardData;

import java.util.LinkedList;
import java.util.List;

public class MainData {
    //private boolean isEnemyLeft;
    private boolean isMyTurn;
    private boolean isMyMove;
    private List<EnemyHandCardData> enemyHandCards = new LinkedList<>();
    private List<CardData> myHandCards = new LinkedList<>();
    private int countOfEnemyHandCards;
    private int cardsLeftInDeck;
    private String actionButtonText;
    private List<FieldCellData> fieldCells = new LinkedList<>();

    public boolean getIsMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public boolean getIsMyMove() {
        return isMyMove;
    }

    public void setMyMove(boolean myMove) {
        isMyMove = myMove;
    }

    public List<EnemyHandCardData> getEnemyHandCards() {
        return enemyHandCards;
    }

    public void setEnemyHandCards(List<EnemyHandCardData> enemyHandCards) {
        this.enemyHandCards = enemyHandCards;
    }

    public List<CardData> getMyHandCards() {
        return myHandCards;
    }

    public void setMyHandCards(List<CardData> myHandCards) {
        this.myHandCards = myHandCards;
    }

    public int getCountOfEnemyHandCards() {
        return countOfEnemyHandCards;
    }

    public void setCountOfEnemyHandCards(int countOfEnemyHandCards) {
        this.countOfEnemyHandCards = countOfEnemyHandCards;
    }

    public int getCardsLeftInDeck() {
        return cardsLeftInDeck;
    }

    public void setCardsLeftInDeck(int cardsLeftInDeck) {
        this.cardsLeftInDeck = cardsLeftInDeck;
    }

    public String getActionButtonText() {
        return actionButtonText;
    }

    public void setActionButtonText(String actionButtonText) {
        this.actionButtonText = actionButtonText;
    }

    public List<FieldCellData> getFieldCells() {
        return fieldCells;
    }

    public void setFieldCells(List<FieldCellData> fieldCells) {
        this.fieldCells = fieldCells;
    }
}
