package com.eventapp.model;

public class SocialEvent extends Event {
    public SocialEvent() {
        this.setEventType("Club Social Event");
    }

    @Override
    public String getSpecificEventRules() {
        return "Casual dress code. Open networking format.";
    }
}