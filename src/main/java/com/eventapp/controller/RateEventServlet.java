package com.eventapp.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.User;
import com.eventapp.dao.ReservationDAO;

@WebServlet("/rate-event")
public class RateEventServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int eventId = Integer.parseInt(request.getParameter("eventId"));
        int rating = Integer.parseInt(request.getParameter("rating"));

        ReservationDAO dao = new ReservationDAO();
        if (dao.rateEvent(eventId, user.getId(), rating)) {
            response.sendRedirect(request.getContextPath() + "/my-tickets?success=rated");
        } else {
            response.sendRedirect(request.getContextPath() + "/my-tickets?error=ratingFailed");
        }
    }
}