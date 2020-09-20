package com.durak.entity;

import com.durak.service.Interface.DeckDAO;
import com.durak.service.Interface.FieldDAO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player1_id", referencedColumnName = "id")
    private Player player1;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player2_id", referencedColumnName = "id")
    private Player player2;
    @OneToOne(cascade = CascadeType.ALL)
    private Field field;
    @OneToOne(cascade = CascadeType.ALL)
    private Deck deck;
    private int whichPlayerTurn;
    private int whichPlayerMove;
    private boolean isStarted;
    private boolean isFinished;
    @Column(name = "player1_leaves")
    private boolean player1Leaves;
    @Column(name = "player2_leaves")
    private boolean player2Leaves;
    private boolean player1Wins;
    private boolean player2Wins;



    public Game(Player player1, DeckDAO deckDAO, FieldDAO fieldDAO) {
        this.player1 = player1;
        this.player1.setGame(this);
        deck = new Deck();
        field = new Field();
        deckDAO.save(deck);
        fieldDAO.save(field);
    }

    public Long getId() {
        return id;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
        this.player2.setGame(this);
        this.isStarted = true;
    }

    public int getWhichPlayerTurn() {
        return whichPlayerTurn;
    }

    public void setWhichPlayerTurn(int whichPlayerTurn) {
        this.whichPlayerTurn = whichPlayerTurn;
    }

    public int getWhichPlayerMove() {
        return whichPlayerMove;
    }

    public void setWhichPlayerMove(int whichPlayerStep) {
        this.whichPlayerMove = whichPlayerStep;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean getPlayer1Leaves() {
        return player1Leaves;
    }

    public void setPlayer1Leaves(boolean player1Leaves) {
        this.player1Leaves = player1Leaves;
    }

    public boolean getPlayer2Leaves() {
        return player2Leaves;
    }

    public void setPlayer2Leaves(boolean player2Leaves) {
        this.player2Leaves = player2Leaves;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public boolean isPlayer1Wins() {
        return player1Wins;
    }

    public void setPlayer1Wins(boolean player1Wins) {
        this.player1Wins = player1Wins;
    }

    public boolean isPlayer2Wins() {
        return player2Wins;
    }

    public void setPlayer2Wins(boolean player2Wins) {
        this.player2Wins = player2Wins;
    }


}
