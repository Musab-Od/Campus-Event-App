package com.eventapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.eventapp.model.Event;
import com.eventapp.util.DBConnection;

import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import com.eventapp.model.EventFactory;

public class EventDAO {

    // CREATE A NEW EVENT
    public boolean createEvent(Event event) {
        // The SQL Insert Statement
        String query = "INSERT INTO events (organizer_id, title, description, department_club, " +
                       "event_date, location, capacity, available_seats, category, event_type, status) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'OPEN')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, event.getOrganizerId());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getDescription());
            stmt.setString(4, event.getDepartmentClub());
            
            // Convert Java's LocalDateTime to SQL's Timestamp
            stmt.setTimestamp(5, Timestamp.valueOf(event.getEventDate()));
            
            stmt.setString(6, event.getLocation());
            stmt.setInt(7, event.getCapacity());
            
            // CRITICAL: When an event is first created, available seats MUST equal total capacity!
            stmt.setInt(8, event.getCapacity()); 
            
            stmt.setString(9, event.getCategory());
            stmt.setString(10, event.getEventType());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // FETCH EVENTS FOR A SPECIFIC ORGANIZER
    public List<Event> getEventsByOrganizer(int organizerId) {
        // Trigger auto-expiration first!
        autoExpireEvents();
        List<Event> events = new ArrayList<>();
        // Pull the newest events first
        String query = "SELECT * FROM events WHERE organizer_id = ? ORDER BY event_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, organizerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // We use the Factory to rebuild the object when pulling from the DB!
                Event event = EventFactory.createEvent(rs.getString("event_type"));
                
                event.setId(rs.getInt("id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setDepartmentClub(rs.getString("department_club"));
                event.setEventDate(rs.getTimestamp("event_date").toLocalDateTime());
                event.setLocation(rs.getString("location"));
                event.setCapacity(rs.getInt("capacity"));
                event.setAvailableSeats(rs.getInt("available_seats"));
                event.setCategory(rs.getString("category"));
                event.setStatus(rs.getString("status"));
                
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    // DELETE AN EVENT (Securely checks organizer ID)
    public boolean deleteEvent(int eventId, int organizerId) {
        String query = "DELETE FROM events WHERE id = ? AND organizer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, organizerId); // Security check!

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // FETCH A SINGLE EVENT (For the Edit Form)
    public Event getEventById(int eventId, int organizerId) {
        String query = "SELECT * FROM events WHERE id = ? AND organizer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, eventId);
            stmt.setInt(2, organizerId); // Security: Ensure this organizer actually owns it
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Event event = EventFactory.createEvent(rs.getString("event_type"));
                event.setId(rs.getInt("id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setDepartmentClub(rs.getString("department_club"));
                event.setEventDate(rs.getTimestamp("event_date").toLocalDateTime());
                event.setLocation(rs.getString("location"));
                event.setCapacity(rs.getInt("capacity"));
                event.setAvailableSeats(rs.getInt("available_seats"));
                event.setCategory(rs.getString("category"));
                event.setStatus(rs.getString("status"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE AN EXISTING EVENT
    public boolean updateEvent(Event event) {
        // NOT updating capacity, available_seats, or event_type to protect database integrity!
        String query = "UPDATE events SET title = ?, description = ?, department_club = ?, " +
                       "event_date = ?, location = ?, category = ?, status = ? " +
                       "WHERE id = ? AND organizer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getDepartmentClub());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEventDate()));
            stmt.setString(5, event.getLocation());
            stmt.setString(6, event.getCategory());
            stmt.setString(7, event.getStatus());
            stmt.setInt(8, event.getId());
            stmt.setInt(9, event.getOrganizerId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // FETCH ALL OPEN EVENTS (For the Student Dashboard)
    public List<Event> getAllOpenEvents() {
        // Trigger auto-expiration first!
        autoExpireEvents();
        List<Event> events = new ArrayList<>();
        // We only want OPEN events, and we want the soonest ones first!
        String query = "SELECT * FROM events WHERE status = 'OPEN' AND event_date > NOW() ORDER BY event_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event event = EventFactory.createEvent(rs.getString("event_type"));
                
                event.setId(rs.getInt("id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setDepartmentClub(rs.getString("department_club"));
                event.setEventDate(rs.getTimestamp("event_date").toLocalDateTime());
                event.setLocation(rs.getString("location"));
                event.setCapacity(rs.getInt("capacity"));
                event.setAvailableSeats(rs.getInt("available_seats"));
                event.setCategory(rs.getString("category"));
                event.setStatus(rs.getString("status"));
                
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    // AUTO-EXPIRE EVENTS THAT HAVE PASSED
    public void autoExpireEvents() {
        // This query finds any OPEN event where the date is in the past, and locks it to EXPIRED
        String query = "UPDATE events SET status = 'EXPIRED' WHERE status = 'OPEN' AND event_date < NOW()";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // FETCH EVENTS RESERVED BY A SPECIFIC STUDENT
    public List<Event> getEventsByStudent(int studentId) {
        autoExpireEvents(); // Keep the database clean!
        
        List<Event> events = new ArrayList<>();
        // We join the tables to only grab events where this specific student has a 'RESERVED' status
        String query = "SELECT e.* FROM events e " +
                       "JOIN reservations r ON e.id = r.event_id " +
                       "WHERE r.student_id = ? AND r.reservation_status = 'RESERVED' " +
                       "ORDER BY e.event_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = EventFactory.createEvent(rs.getString("event_type"));
                event.setId(rs.getInt("id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setDepartmentClub(rs.getString("department_club"));
                event.setEventDate(rs.getTimestamp("event_date").toLocalDateTime());
                event.setLocation(rs.getString("location"));
                event.setCapacity(rs.getInt("capacity"));
                event.setAvailableSeats(rs.getInt("available_seats"));
                event.setCategory(rs.getString("category"));
                event.setStatus(rs.getString("status"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
}