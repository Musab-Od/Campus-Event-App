package com.eventapp.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.User;
import com.eventapp.model.Event;
import com.eventapp.dao.UserDAO;
import com.eventapp.dao.EventDAO;

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // SECURITY: Only let Admins in!
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Fetch everything for the master tables
        List<User> allUsers = userDAO.getAllUsers();
        List<Event> allEvents = eventDAO.getAllEventsForAdmin();

        request.setAttribute("users", allUsers);
        request.setAttribute("events", allEvents);

        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
}