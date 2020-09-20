package com.durak.viewData.CardData;

public class CardData {
    private long id;
    private String path;
    private String divClass = "";
    private boolean isTrump;


    public CardData(long id, String path,boolean isTrump) {
        this.id = id;
        this.path = path;
        this.isTrump = isTrump;
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
    public boolean getIsTrump() {
        return isTrump;
    }

    public void setIsTrump(boolean trump) {
        isTrump = trump;
    }

}
