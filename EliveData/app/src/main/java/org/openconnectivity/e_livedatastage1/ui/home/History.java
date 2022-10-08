package org.openconnectivity.e_livedatastage1.ui.home;

public class History {
    private String liveContent;
    private String liveTime;
    private String liveDuration;

    public History(String liveContent, String liveTime,String liveDuration){
        this.liveContent = liveContent;
        this.liveTime = liveTime;
        this.liveDuration = liveDuration;
    }

    public String getLiveContent() {
        return liveContent;
    }

    public String getLiveTime() {
        return liveTime;
    }

    public String getLiveDuration() {
        return liveDuration;
    }
}
