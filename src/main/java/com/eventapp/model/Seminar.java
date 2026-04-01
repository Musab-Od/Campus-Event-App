package com.eventapp.model;

public class Seminar extends Event {
    public Seminar() {
        this.setEventType("Seminar");
    }

    @Override
    public String getSpecificEventRules() {
        return "Seminars are lecture-based. Q&A will be held at the end.";
    }
}