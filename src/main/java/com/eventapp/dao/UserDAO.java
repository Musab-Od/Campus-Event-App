package com.eventapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eventapp.model.User;
import com.eventapp.util.DBConnection;

public class UserDAO {

    // 1. REGISTER A NEW USER
    public boolean registerUser(User user) {
        String query = "INSERT INTO users (name, email, password, faculty, department, admission_year, role) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFaculty());
            stmt.setString(5, user.getDepartment());
            stmt.setInt(6, user.getAdmissionYear());
            stmt.setString(7, user.getRole());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. AUTHENTICATE LOGIN
    public User authenticateUser(String email, String password) {
        // Query already safely includes: AND is_blocked = FALSE
        String query = "SELECT * FROM users WHERE email = ? AND password = ? AND is_blocked = FALSE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("faculty"),
                    rs.getString("department"),
                    rs.getInt("admission_year"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // Login failed or user is blocked
    }

    // 3. UPDATE USER PROFILE
    public boolean updateUser(User user) {
        String query = "UPDATE users SET name = ?, password = ?, faculty = ?, department = ?, admission_year = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFaculty());
            stmt.setString(4, user.getDepartment());
            stmt.setInt(5, user.getAdmissionYear());
            stmt.setInt(6, user.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. FETCH ALL USERS (FOR ADMIN DASHBOARD)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        // We exclude the ADMIN so the admin can't accidentally block themselves!
        String query = "SELECT * FROM users WHERE role != 'ADMIN' ORDER BY role, name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User u = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("faculty"),
                    rs.getString("department"),
                    rs.getInt("admission_year"),
                    rs.getString("role")
                );
                // Attach the blocked status from the database to the object
                u.setBlocked(rs.getBoolean("is_blocked")); 
                users.add(u);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return users;
    }

    // 5. TOGGLE BLOCK STATUS (FOR ADMIN DASHBOARD)
    public boolean toggleBlockStatus(int userId, boolean block) {
        String query = "UPDATE users SET is_blocked = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, block);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}