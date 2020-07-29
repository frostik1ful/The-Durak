package com.durak.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Suit suit;
    private Value value;
    @ManyToOne
    @JoinColumn(name = "deck_id")
    private Deck deck;
    @ManyToOne()
    @JoinColumn(name = "player_id")
    private Player player;

    public Card(Suit suit, Value value,Deck deck) {
        this.suit = suit;
        this.value = value;
        this.deck=deck;
    }

    public Card() {
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", suit=" + suit +
                ", value=" + value +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public enum Value {
        SIX(1), SEVEN(2), EIGHT(3), NINE(4), TEN(5), JACK(6), QUEEN(7), KING(8), ACE(9);
        private int val;


        Value(int n) {
            this.val=n;
        }

        public int getVal() {
            return val;
        }
    }

    public enum Suit {
        CLUBS, HEARTS, DIAMONDS, SPADES
    }

}

