package com.eventapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.User;
import com.eventapp.model.Event;
import com.eventapp.dao.EventDAO;
import com.eventapp.strategy.*;

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
        EventDAO eventDAO = new EventDAO();
        List<Event> eventsToDisplay;

        // 2. Fetch the base list of events based on role
        if ("ORGANIZER".equals(user.getRole())) {
            eventsToDisplay = eventDAO.getEventsByOrganizer(user.getId());
        } else {
            eventsToDisplay = eventDAO.getAllOpenEvents();
        }

        // 3. CHECK FOR SEARCH PARAMETERS & APPLY STRATEGY
        String filterType = request.getParameter("filterType");
        String query = request.getParameter("query");

        if (filterType != null && !filterType.isEmpty()) {
            EventFilterStrategy strategy = null;

            // Pick the right tool based on the dropdown menu
            switch (filterType) {
                case "title": strategy = new TitleFilterStrategy(); break;
                case "department": strategy = new DepartmentFilterStrategy(); break;
                case "category": strategy = new CategoryFilterStrategy(); break;
                case "type": strategy = new TypeFilterStrategy(); break;
                case "date": strategy = new DateFilterStrategy(); break;
                case "availability": strategy = new AvailabilityFilterStrategy(); break;
            }

            // Filter the list if a valid strategy was picked
            if (strategy != null) {
                eventsToDisplay = strategy.filter(eventsToDisplay, query);
            }
        }

        // 4. Route them to the correct view with the filtered data
        if ("ORGANIZER".equals(user.getRole())) {
            
            // We use your original attribute name "myEvents" so JSP doesn't break
            request.setAttribute("myEvents", eventsToDisplay);
            request.getRequestDispatcher("/WEB-INF/views/dashboard-organizer.jsp").forward(request, response);
            
        } else if ("STUDENT".equals(user.getRole())) {
            
            // Fetch the events this specific student has already booked
            List<Event> myTickets = eventDAO.getEventsByStudent(user.getId());
            Set<Integer> bookedIds = new HashSet<>();
            for(Event e : myTickets) {
                bookedIds.add(e.getId());
            }
            
            // We use your original attribute name "openEvents" so JSP doesn't break
            request.setAttribute("openEvents", eventsToDisplay); 
            request.setAttribute("bookedIds", bookedIds);
            
            request.getRequestDispatcher("/WEB-INF/views/dashboard-student.jsp").forward(request, response);
            
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}