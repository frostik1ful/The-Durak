package com.durak.viewData.CardData;

public class UpperCardData extends CardData{
    public UpperCardData(long id, String path,boolean isTrump) {
        super(id, path,isTrump);
        setDivClass("gameCard upperCard tableCard");
    }
}
