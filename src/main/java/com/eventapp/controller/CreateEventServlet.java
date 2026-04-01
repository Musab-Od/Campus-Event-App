package com.eventapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.Event;
import com.eventapp.model.EventFactory;
import com.eventapp.model.User;
import com.eventapp.dao.EventDAO;

@WebServlet("/create-event")
public class CreateEventServlet extends HttpServlet {
    
    private EventDAO eventDAO = new EventDAO();

    // Show the Create Event Form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // SECURITY 1: Are they logged in?
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // SECURITY 2: Are they an Organizer?
        User user = (User) session.getAttribute("user");
        if (!"ORGANIZER".equals(user.getRole())) {
            // Throw a 403 Forbidden error if a regular Student tries to hack the URL
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only Organizers can create events.");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/create-event.jsp").forward(request, response);
    }

    // Process the Form Submission
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Grab the logged-in organizer from the session
        HttpSession session = request.getSession();
        User organizer = (User) session.getAttribute("user");

        // 1. Grab all the text from the HTML form
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String departmentClub = request.getParameter("departmentClub");
        String location = request.getParameter("location");
        int capacity = Integer.parseInt(request.getParameter("capacity"));
        String category = request.getParameter("category");
        String eventType = request.getParameter("eventType");
        
        // The HTML form sends date/time as a String. We parse it to Java's LocalDateTime.
        String eventDateStr = request.getParameter("eventDate");
        LocalDateTime eventDate = LocalDateTime.parse(eventDateStr);

        try {
            // 2. We hand the string to the Factory, and it gives us the right object.
            Event newEvent = EventFactory.createEvent(eventType);
            
            // 3. Fill the container with the rest of the data
            newEvent.setOrganizerId(organizer.getId());
            newEvent.setTitle(title);
            newEvent.setDescription(description);
            newEvent.setDepartmentClub(departmentClub);
            newEvent.setLocation(location);
            newEvent.setCapacity(capacity);
            newEvent.setCategory(category);
            newEvent.setEventDate(eventDate);
            
            // 4. Save to Database using the DAO
            boolean isCreated = eventDAO.createEvent(newEvent);
            
            if (isCreated) {
                // Success! Send them to the dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard?success=eventCreated");
            } else {
                request.setAttribute("errorMessage", "Database error. Could not create event.");
                request.getRequestDispatcher("/WEB-INF/views/create-event.jsp").forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid Event Type selected.");
            request.getRequestDispatcher("/WEB-INF/views/create-event.jsp").forward(request, response);
        }
    }
}