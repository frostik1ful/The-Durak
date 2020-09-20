package com.durak.viewData;

import com.durak.viewData.CardData.CardData;

public class FieldCellData {
    private CardData bottomCard;
    private CardData upperCard;

    public FieldCellData(CardData bottomCard, CardData upperCard) {
        this.bottomCard = bottomCard;
        this.upperCard = upperCard;
    }

    public CardData getBottomCard() {
        return bottomCard;
    }

    public void setBottomCard(CardData bottomCard) {
        this.bottomCard = bottomCard;
    }

    public CardData getUpperCard() {
        return upperCard;
    }

    public void setUpperCard(CardData upperCard) {
        this.upperCard = upperCard;
    }
}
