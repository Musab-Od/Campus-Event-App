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
import com.eventapp.dao.EventDAO;

@WebServlet("/my-tickets")
public class MyTicketsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"STUDENT".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only students can view tickets.");
            return;
        }

        // Fetch their specific reserved events
        EventDAO eventDAO = new EventDAO();
        List<Event> myTickets = eventDAO.getEventsByStudent(user.getId());
        
        request.setAttribute("myTickets", myTickets);
        request.getRequestDispatcher("/WEB-INF/views/my-tickets.jsp").forward(request, response);
    }
}