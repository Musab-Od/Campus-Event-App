package com.eventapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.eventapp.util.DBConnection;

public class ReservationDAO {

public String bookTicket(int eventId, int studentId) {
        
        // 1. Grab the status so we know IF they cancelled before
        String checkDuplicateQuery = "SELECT reservation_status FROM reservations WHERE event_id = ? AND student_id = ?";
        
        String checkTimeConflictQuery = 
            "SELECT r.id FROM reservations r JOIN events e ON r.event_id = e.id " +
            "WHERE r.student_id = ? AND r.reservation_status = 'RESERVED' " +
            "AND e.event_date = (SELECT event_date FROM events WHERE id = ?)";

        String checkSeatsQuery = "SELECT available_seats FROM events WHERE id = ? FOR UPDATE";
        
        // We now have TWO ways to save the reservation
        String insertQuery = "INSERT INTO reservations (event_id, student_id, reservation_status) VALUES (?, ?, 'RESERVED')";
        String reactivateQuery = "UPDATE reservations SET reservation_status = 'RESERVED' WHERE event_id = ? AND student_id = ?";
        
        String updateEventQuery = "UPDATE events SET available_seats = available_seats - 1 WHERE id = ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. CHECK FOR DUPLICATES & PREVIOUS CANCELLATIONS
            boolean wasCancelled = false;
            try (PreparedStatement checkDupStmt = conn.prepareStatement(checkDuplicateQuery)) {
                checkDupStmt.setInt(1, eventId);
                checkDupStmt.setInt(2, studentId);
                ResultSet rsDup = checkDupStmt.executeQuery();
                if (rsDup.next()) {
                    String status = rsDup.getString("reservation_status");
                    if ("RESERVED".equals(status)) {
                        conn.rollback();
                        return "ALREADY_BOOKED";
                    } else if ("CANCELLED".equals(status)) {
                        wasCancelled = true; // They cancelled before! We will reactivate this row later.
                    }
                }
            }

            // 2. CHECK FOR TIME CONFLICTS
            try (PreparedStatement checkTimeStmt = conn.prepareStatement(checkTimeConflictQuery)) {
                checkTimeStmt.setInt(1, studentId);
                checkTimeStmt.setInt(2, eventId);
                ResultSet rsTime = checkTimeStmt.executeQuery();
                if (rsTime.next()) {
                    conn.rollback();
                    return "TIME_CONFLICT";
                }
            }

            // 3. LOCK THE ROW AND CHECK SEATS
            try (PreparedStatement checkSeatsStmt = conn.prepareStatement(checkSeatsQuery)) {
                checkSeatsStmt.setInt(1, eventId);
                ResultSet rsSeats = checkSeatsStmt.executeQuery();
                if (rsSeats.next()) {
                    int availableSeats = rsSeats.getInt("available_seats");
                    if (availableSeats <= 0) {
                        conn.rollback();
                        return "SOLD_OUT";
                    }
                } else {
                    conn.rollback();
                    return "EVENT_NOT_FOUND";
                }
            }

            // 4. SAVE THE RESERVATION (Insert new OR Update cancelled)
            if (wasCancelled) {
                try (PreparedStatement reactivateStmt = conn.prepareStatement(reactivateQuery)) {
                    reactivateStmt.setInt(1, eventId);
                    reactivateStmt.setInt(2, studentId);
                    reactivateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, eventId);
                    insertStmt.setInt(2, studentId);
                    insertStmt.executeUpdate();
                }
            }

            // 5. DECREASE THE SEAT COUNT
            try (PreparedStatement updateEventStmt = conn.prepareStatement(updateEventQuery)) {
                updateEventStmt.setInt(1, eventId);
                updateEventStmt.executeUpdate();
            }

            conn.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return "ERROR";
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    // CANCEL A TICKET
    public boolean cancelTicket(int eventId, int studentId) {
        
// We JOIN the events table so we can check the clock! The event_date MUST be in the future ( > NOW() ).
String checkQuery = "SELECT r.id FROM reservations r JOIN events e ON r.event_id = e.id " +
                    "WHERE r.event_id = ? AND r.student_id = ? " +
                    "AND r.reservation_status = 'RESERVED' AND e.event_date > NOW()";
        // 2. Change the status instead of deleting the row (keeps a good history!)
        String cancelQuery = "UPDATE reservations SET reservation_status = 'CANCELLED' WHERE event_id = ? AND student_id = ?";
        // 3. Give the seat back!
        String refundSeatQuery = "UPDATE events SET available_seats = available_seats + 1 WHERE id = ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // Step A: Verify they actually have an active reservation
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, eventId);
                checkStmt.setInt(2, studentId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false; // No active reservation found
                }
            }

            // Step B: Mark as Cancelled
            try (PreparedStatement cancelStmt = conn.prepareStatement(cancelQuery)) {
                cancelStmt.setInt(1, eventId);
                cancelStmt.setInt(2, studentId);
                cancelStmt.executeUpdate();
            }

            // Step C: Increase available seats
            try (PreparedStatement refundStmt = conn.prepareStatement(refundSeatQuery)) {
                refundStmt.setInt(1, eventId);
                refundStmt.executeUpdate();
            }

            conn.commit(); // Save changes!
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // MARK ATTENDANCE (Present or Absent)
    public boolean updateAttendance(int eventId, int studentId, String attendanceStatus) {
        // attendanceStatus should be 'PRESENT' or 'ABSENT'
        String query = "UPDATE reservations SET attendance_status = ? WHERE event_id = ? AND student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setString(1, attendanceStatus);
            stmt.setInt(2, eventId);
            stmt.setInt(3, studentId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // FETCH ALL ATTENDEES FOR AN EVENT
    public java.util.List<com.eventapp.model.Attendee> getAttendeesForEvent(int eventId) {
        java.util.List<com.eventapp.model.Attendee> attendees = new java.util.ArrayList<>();
        
        // Join users and reservations to get names and statuses!
        String query = "SELECT u.id, u.name, u.email, r.attendance_status " +
                       "FROM users u " +
                       "JOIN reservations r ON u.id = r.student_id " +
                       "WHERE r.event_id = ? AND r.reservation_status = 'RESERVED'";
                       
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attendees.add(new com.eventapp.model.Attendee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("attendance_status") != null ? rs.getString("attendance_status") : "PENDING"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendees;
    }
    // SAVE STUDENT RATING
    public boolean rateEvent(int eventId, int studentId, int rating) {
        String query = "UPDATE reservations SET rating = ? WHERE event_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rating);
            stmt.setInt(2, eventId);
            stmt.setInt(3, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}