package com.durak.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class CardCell {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name = "field_id")
    private Field field;
    @OneToOne(cascade = CascadeType.ALL)
    private Card bottomCard;
    @OneToOne(cascade = CascadeType.ALL)
    private Card upperCard;

    public CardCell(Card bottomCard) {
        this.bottomCard = bottomCard;
    }


    public Card getBottomCard() {
        return bottomCard;
    }

    public void setBottomCard(Card puttableCard) {
        this.bottomCard = puttableCard;
    }

    public Card getUpperCard() {
        return upperCard;
    }

    public void setUpperCard(Card upperCard) {
        this.upperCard = upperCard;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
