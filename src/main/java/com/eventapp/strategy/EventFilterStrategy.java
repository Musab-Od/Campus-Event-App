package com.eventapp.strategy;

import com.eventapp.model.Event;
import java.util.List;

public interface EventFilterStrategy {
    List<Event> filter(List<Event> events, String criteria);
}