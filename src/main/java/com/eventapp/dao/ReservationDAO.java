package com.eventapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.eventapp.util.DBConnection;

public class ReservationDAO {

    public String bookTicket(int eventId, int studentId) {
        
        String checkDuplicateQuery = "SELECT id FROM reservations WHERE event_id = ? AND student_id = ?";
        // "FOR UPDATE" locks this row so nobody else can steal the last seat!
        String checkSeatsQuery = "SELECT available_seats FROM events WHERE id = ? FOR UPDATE";
        String reserveQuery = "INSERT INTO reservations (event_id, student_id) VALUES (?, ?)";
        String updateEventQuery = "UPDATE events SET available_seats = available_seats - 1 WHERE id = ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            
            // 1. START TRANSACTION (Turn off auto-save)
            conn.setAutoCommit(false); 

            // 2. CHECK FOR DUPLICATES
            try (PreparedStatement checkDupStmt = conn.prepareStatement(checkDuplicateQuery)) {
                checkDupStmt.setInt(1, eventId);
                checkDupStmt.setInt(2, studentId);
                ResultSet rsDup = checkDupStmt.executeQuery();
                if (rsDup.next()) {
                    conn.rollback(); // Cancel transaction
                    return "ALREADY_BOOKED";
                }
            }

            // 3. LOCK THE ROW AND CHECK SEATS
            try (PreparedStatement checkSeatsStmt = conn.prepareStatement(checkSeatsQuery)) {
                checkSeatsStmt.setInt(1, eventId);
                ResultSet rsSeats = checkSeatsStmt.executeQuery();
                
                if (rsSeats.next()) {
                    int availableSeats = rsSeats.getInt("available_seats");
                    
                    if (availableSeats <= 0) {
                        conn.rollback(); // Cancel transaction
                        return "SOLD_OUT";
                    }
                } else {
                    conn.rollback();
                    return "EVENT_NOT_FOUND";
                }
            }

            // 4. INSERT THE RESERVATION
            try (PreparedStatement reserveStmt = conn.prepareStatement(reserveQuery)) {
                reserveStmt.setInt(1, eventId);
                reserveStmt.setInt(2, studentId);
                reserveStmt.executeUpdate();
            }

            // 5. DECREASE THE SEAT COUNT
            try (PreparedStatement updateEventStmt = conn.prepareStatement(updateEventQuery)) {
                updateEventStmt.setInt(1, eventId);
                updateEventStmt.executeUpdate();
            }

            // 6. SUCCESS! SAVE THE TRANSACTION
            conn.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // If anything crashes, undo everything!
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return "ERROR";
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
}