package com.eventapp.model;

public class Workshop extends Event {
    public Workshop() {
        this.setEventType("Workshop");
    }

    @Override
    public String getSpecificEventRules() {
        return "Workshops require hands-on participation and laptop setup.";
    }
}