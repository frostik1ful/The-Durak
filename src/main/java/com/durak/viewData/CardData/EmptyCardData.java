package com.durak.viewData.CardData;

import com.durak.viewData.CardData.CardData;

public class EmptyCardData extends CardData {

    public EmptyCardData(long id, String path) {
        super(id, path,false);
        setDivClass("empty");

    }
    public EmptyCardData(){
        setDivClass("empty");
    }
}
