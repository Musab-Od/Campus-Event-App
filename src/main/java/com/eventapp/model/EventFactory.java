package com.eventapp.model;

public class EventFactory {

    // The Factory Method
    public static Event createEvent(String eventType) {
        
        if (eventType == null || eventType.isEmpty()) {
            throw new IllegalArgumentException("Event type cannot be null.");
        }

        // The Factory decides which object to build based on the string passed to it
        switch (eventType) {
            case "Workshop":
                return new Workshop();
            case "Seminar":
                return new Seminar();
            case "Club Social Event":
                return new SocialEvent();
            case "Sports Activity":
                return new SportsActivity();
            default:
                throw new IllegalArgumentException("Unknown Event Type: " + eventType);
        }
    }
}