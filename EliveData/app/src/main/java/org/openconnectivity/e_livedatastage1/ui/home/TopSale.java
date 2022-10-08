package org.openconnectivity.e_livedatastage1.ui.home;

public class TopSale {

    private int imageId;
    private String name;

    public TopSale(int imageId, String name){
        this.name = name;
        this.imageId = imageId;
    }

    public String getName(){
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
