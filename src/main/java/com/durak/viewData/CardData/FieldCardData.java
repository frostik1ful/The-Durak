package com.durak.viewData.CardData;

public class FieldCardData extends CardData{
    public FieldCardData(long id, String path,boolean isTrump) {
        super(id, path,isTrump);
        setDivClass("gameCard tableCard");
    }
}
