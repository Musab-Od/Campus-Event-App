package com.eventapp.strategy;

import com.eventapp.model.Event;
import java.util.List;
import java.util.stream.Collectors;

public class TitleFilterStrategy implements EventFilterStrategy {
    
    @Override
    public List<Event> filter(List<Event> events, String criteria) {
        // If the user didn't type anything, just return the whole list
        if (criteria == null || criteria.trim().isEmpty()) {
            return events;
        }
        
        // Filter events where the title contains the search criteria (case-insensitive)
        return events.stream()
                .filter(e -> e.getTitle().toLowerCase().contains(criteria.toLowerCase()))
                .collect(Collectors.toList());
    }
}