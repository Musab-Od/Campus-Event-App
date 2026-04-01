package com.eventapp.model;

public class SportsActivity extends Event {
    public SportsActivity() {
        this.setEventType("Sports Activity");
    }

    @Override
    public String getSpecificEventRules() {
        return "Must wear athletic gear and sign a physical liability waiver.";
    }
}