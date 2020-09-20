package com.durak.viewData.CardData;

public class BottomCardData  extends CardData{
    public BottomCardData(long id, String path,boolean isTrump) {
        super(id, path,isTrump);
        setDivClass("gameCard tableCard bottomCard");
    }
}
