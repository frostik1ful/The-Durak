package com.durak.viewData.CardData;

public class CardData {
    private long id;
    private String path;
    private String divClass = "";


    public CardData(long id, String path) {
        this.id = id;
        this.path = path;
    }
    public CardData(){

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDivClass() {
        return divClass;
    }

    public void setDivClass(String divClass) {
        this.divClass = divClass;
    }


}
