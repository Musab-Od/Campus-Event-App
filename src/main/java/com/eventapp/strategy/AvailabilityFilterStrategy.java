package com.eventapp.strategy;

import com.eventapp.model.Event;
import java.util.List;
import java.util.stream.Collectors;

public class AvailabilityFilterStrategy implements EventFilterStrategy {
    @Override
    public List<Event> filter(List<Event> events, String criteria) {
        return events.stream()
                .filter(e -> e.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }
}