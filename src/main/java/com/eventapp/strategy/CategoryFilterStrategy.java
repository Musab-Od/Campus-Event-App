package com.eventapp.strategy;

import com.eventapp.model.Event;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryFilterStrategy implements EventFilterStrategy {
    @Override
    public List<Event> filter(List<Event> events, String criteria) {
        if (criteria == null || criteria.trim().isEmpty()) return events;

        return events.stream()
                .filter(e -> e.getCategory().toLowerCase().contains(criteria.toLowerCase()))
                .collect(Collectors.toList());
    }
}