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
    //private int countOfEnemyHandCards;
    //private int cardsLeftInDeck;
    private String enemyName;
    private String actionButtonText;
    private List<FieldCellData> fieldCells = new LinkedList<>();
    private boolean isIWon;
    private boolean isILose;
    private boolean takesPreLastCard;
    private boolean takesLastCard;

    private boolean enemyTakesPreLastCard;
    private boolean enemyTakesLastCard;



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

//    public int getCountOfEnemyHandCards() {
//        return countOfEnemyHandCards;
//    }
//
//    public void setCountOfEnemyHandCards(int countOfEnemyHandCards) {
//        this.countOfEnemyHandCards = countOfEnemyHandCards;
//    }
//
//    public int getCardsLeftInDeck() {
//        return cardsLeftInDeck;
//    }
//
//    public void setCardsLeftInDeck(int cardsLeftInDeck) {
//        this.cardsLeftInDeck = cardsLeftInDeck;
//    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
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

    public boolean getIsIWon() {
        return isIWon;
    }

    public void setIsIWon(boolean iWon) {
        this.isIWon = iWon;
    }

    public boolean getIsILose() {
        return isILose;
    }

    public void setIsILose(boolean iLose) {
        this.isILose = iLose;
    }

    public boolean getTakesPreLastCard() {
        return takesPreLastCard;
    }

    public void setTakesPreLastCard(boolean takesPreLastCard) {
        this.takesPreLastCard = takesPreLastCard;
    }

    public boolean getTakesLastCard() {
        return takesLastCard;
    }

    public void setTakesLastCard(boolean takesLastCard) {
        this.takesLastCard = takesLastCard;
    }

    public boolean getEnemyTakesPreLastCard() {
        return enemyTakesPreLastCard;
    }

    public void setEnemyTakesPreLastCard(boolean enemyTakesPreLastCard) {
        this.enemyTakesPreLastCard = enemyTakesPreLastCard;
    }

    public boolean getEnemyTakesLastCard() {
        return enemyTakesLastCard;
    }

    public void setEnemyTakesLastCard(boolean enemyTakesLastCard) {
        this.enemyTakesLastCard = enemyTakesLastCard;
    }
}
