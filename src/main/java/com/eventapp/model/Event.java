package com.eventapp.model;

import java.time.LocalDateTime;

public abstract class Event {
    // Database fields matching our schema
    private int id;
    private int organizerId;
    private String title;
    private String description;
    private String departmentClub;
    private LocalDateTime eventDate;
    private String location;
    private int capacity;
    private int availableSeats;
    private String category; // Educational, Social, Sports, Cultural, Technical
    private String eventType; // This will be set by the Factory!
    private String imageUrl;
    private String status;

    // Empty Constructor
    public Event() {}

    // Every specific event type MUST define its own rules.
    public abstract String getSpecificEventRules();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentClub() {
        return departmentClub;
    }

    public void setDepartmentClub(String departmentClub) {
        this.departmentClub = departmentClub;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}