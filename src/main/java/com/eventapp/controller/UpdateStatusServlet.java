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

@WebServlet("/update-status")
public class UpdateStatusServlet extends HttpServlet {

    private EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        // Only organizers can change status
        if (!"ORGANIZER".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String eventIdParam = request.getParameter("id");
        String newStatus = request.getParameter("status");

        if (eventIdParam != null && newStatus != null) {
            int eventId = Integer.parseInt(eventIdParam);
            
            // Only accept valid statuses
            if (newStatus.equals("CLOSED") || newStatus.equals("COMPLETED") || newStatus.equals("OPEN")) {
                boolean success = eventDAO.updateEventStatus(eventId, user.getId(), newStatus);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/dashboard?success=statusUpdated");
                    return;
                }
            }
        }
        
        // If it fails or parameters are missing
        response.sendRedirect(request.getContextPath() + "/dashboard?error=statusUpdateFailed");
    }
}