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
import com.eventapp.model.User;
import com.eventapp.dao.EventDAO;

@WebServlet("/edit-event")
public class EditEventServlet extends HttpServlet {

    private EventDAO eventDAO = new EventDAO();

    // Show the Edit Form with pre-filled data
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String eventIdParam = request.getParameter("id");

        if (eventIdParam != null) {
            int eventId = Integer.parseInt(eventIdParam);
            Event eventToEdit = eventDAO.getEventById(eventId, user.getId());

            if (eventToEdit != null) {
                // Pass the event to the JSP so we can fill the text boxes
                request.setAttribute("event", eventToEdit);
                request.getRequestDispatcher("/WEB-INF/views/edit-event.jsp").forward(request, response);
                return;
            }
        }
        // If someone tampers with the URL, kick them back to the dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    // Process the updated data
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User organizer = (User) session.getAttribute("user");

        int eventId = Integer.parseInt(request.getParameter("id"));
        
        // We pull the old event first so we don't lose the capacity data
        Event updatedEvent = eventDAO.getEventById(eventId, organizer.getId());
        
        if (updatedEvent != null) {
            updatedEvent.setTitle(request.getParameter("title"));
            updatedEvent.setDescription(request.getParameter("description"));
            updatedEvent.setDepartmentClub(request.getParameter("departmentClub"));
            updatedEvent.setLocation(request.getParameter("location"));
            updatedEvent.setCategory(request.getParameter("category"));
            updatedEvent.setStatus(request.getParameter("status"));
            
            String eventDateStr = request.getParameter("eventDate");
            updatedEvent.setEventDate(LocalDateTime.parse(eventDateStr));

            if (eventDAO.updateEvent(updatedEvent)) {
                response.sendRedirect(request.getContextPath() + "/dashboard?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?error=updateFailed");
            }
        }
    }
}