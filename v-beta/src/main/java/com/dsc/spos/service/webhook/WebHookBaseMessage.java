package com.dsc.spos.service.webhook;

public class WebHookBaseMessage {
    private String eId;
    private String appId;
    private String event;
    private String eventName;
    private String eventId;
    public String geteId() {
        return eId;
    }
    public void seteId(String eId) {
        this.eId = eId;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


}
