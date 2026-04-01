package com.eventapp.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.User;
import com.eventapp.dao.EventDAO;

@WebServlet("/delete-event")
public class DeleteEventServlet extends HttpServlet {

    private EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Security Check: Are they logged in as an Organizer?
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"ORGANIZER".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized action.");
            return;
        }

        // 2. Grab the event ID from the URL (e.g., /delete-event?id=5)
        String eventIdParam = request.getParameter("id");
        
        if (eventIdParam != null && !eventIdParam.isEmpty()) {
            int eventId = Integer.parseInt(eventIdParam);
            
            // 3. Attempt to delete
            boolean isDeleted = eventDAO.deleteEvent(eventId, user.getId());
            
            if (isDeleted) {
                response.sendRedirect(request.getContextPath() + "/dashboard?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?error=deleteFailed");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}