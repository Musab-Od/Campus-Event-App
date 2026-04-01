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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Verify the user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // 2. Route them to the correct view based on their role
        if ("ORGANIZER".equals(user.getRole())) {
            
            // Fetch this organizer's events from the database
            EventDAO eventDAO = new EventDAO();
            List<Event> myEvents = eventDAO.getEventsByOrganizer(user.getId());
            request.setAttribute("myEvents", myEvents);
            
            request.getRequestDispatcher("/WEB-INF/views/dashboard-organizer.jsp").forward(request, response);
            
        } else if ("STUDENT".equals(user.getRole())) {
            
            EventDAO eventDAO = new EventDAO();
            
            // 1. Fetch ALL open events
            java.util.List<com.eventapp.model.Event> openEvents = eventDAO.getAllOpenEvents();
            
            // 2. Fetch the events this specific student has already booked
            java.util.List<com.eventapp.model.Event> myTickets = eventDAO.getEventsByStudent(user.getId());
            
            // 3. Extract just the IDs into a Set so the JSP page can read them easily
            java.util.Set<Integer> bookedIds = new java.util.HashSet<>();
            for(com.eventapp.model.Event e : myTickets) {
                bookedIds.add(e.getId());
            }
            
            request.setAttribute("openEvents", openEvents);
            request.setAttribute("bookedIds", bookedIds);
            
            request.getRequestDispatcher("/WEB-INF/views/dashboard-student.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}