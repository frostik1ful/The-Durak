package com.durak.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "field")
    private List<CardCell> cardCells;

    public List<CardCell> getCardCells() {
        return cardCells;
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                '}';
    }
}
