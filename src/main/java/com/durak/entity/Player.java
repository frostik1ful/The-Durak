package com.durak.entity;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private boolean takesPreLastCard;
    private boolean takesLastCard;
    private boolean cardsReceived;
    @OneToOne(cascade = CascadeType.ALL)
    private Game game;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private List<Card> cards = new LinkedList<>();

    public Player(User user){
        this.name=user.getName();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public boolean isCardsReceived() {
        return cardsReceived;
    }

    public void setCardsReceived(boolean cardsReceived) {
        this.cardsReceived = cardsReceived;
    }

    public List<Card> getPlayerCards() {
        return cards;
    }

    public void setPlayerCards(List<Card> playerCards) {
        this.cards = playerCards;
    }
}
