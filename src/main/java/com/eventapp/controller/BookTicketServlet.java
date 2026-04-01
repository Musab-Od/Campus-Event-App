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

@WebServlet("/book-ticket")
public class BookTicketServlet extends HttpServlet {

    private ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Security: Are they logged in as a Student?
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"STUDENT".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only students can book tickets.");
            return;
        }

        // 2. Grab the Event ID from the hidden form input
        String eventIdParam = request.getParameter("eventId");
        if (eventIdParam == null || eventIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        int eventId = Integer.parseInt(eventIdParam);

        // 3. Attempt the booking using our Concurrency-Safe DAO!
        String result = reservationDAO.bookTicket(eventId, user.getId());

        // 4. Redirect based on the strict outcome
        switch (result) {
            case "SUCCESS":
                response.sendRedirect(request.getContextPath() + "/dashboard?success=ticketBooked");
                break;
            case "ALREADY_BOOKED":
                response.sendRedirect(request.getContextPath() + "/dashboard?error=alreadyBooked");
                break;
            case "SOLD_OUT":
                response.sendRedirect(request.getContextPath() + "/dashboard?error=soldOut");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/dashboard?error=systemError");
                break;
        }
    }
}