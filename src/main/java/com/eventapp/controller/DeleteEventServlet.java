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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int eventId = Integer.parseInt(request.getParameter("id"));
        
        EventDAO dao = new EventDAO();
        boolean isDeleted = false;

        // VIP CHECK: Admin vs Organizer
        if ("ADMIN".equals(user.getRole())) {
            isDeleted = dao.deleteEventForAdmin(eventId);
        } else if ("ORGANIZER".equals(user.getRole())) {
            isDeleted = dao.deleteEvent(eventId, user.getId());
        }

        // Redirect back to the correct dashboard
        if (isDeleted) {
            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin-dashboard?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?success=deleted");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard?error=deleteFailed");
        }
    }
}