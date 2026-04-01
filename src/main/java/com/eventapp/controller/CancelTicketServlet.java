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

@WebServlet("/cancel-ticket")
public class CancelTicketServlet extends HttpServlet {

    private ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"STUDENT".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only students can cancel tickets.");
            return;
        }

        String eventIdParam = request.getParameter("eventId");
        if (eventIdParam != null && !eventIdParam.isEmpty()) {
            int eventId = Integer.parseInt(eventIdParam);
            
            boolean isCancelled = reservationDAO.cancelTicket(eventId, user.getId());
            
            if (isCancelled) {
                response.sendRedirect(request.getContextPath() + "/dashboard?success=ticketCancelled");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?error=cancelFailed");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}