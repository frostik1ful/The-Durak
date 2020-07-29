package com.durak.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity
@Data
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deck")
    private List<Card> cards = new LinkedList<>();
    private Card.Suit trump;

    public Deck() {
        addCards();
    }

    private void addCards() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Value value : Card.Value.values()) {
                cards.add(new Card(suit, value, this));
            }
        }
        Collections.shuffle(cards);
        this.trump = cards.get(cards.size() - 1).getSuit();


    }

    public List<Card> getCards() {
        return cards;
    }

    public Card.Suit getTrump() {
        return trump;
    }
}
