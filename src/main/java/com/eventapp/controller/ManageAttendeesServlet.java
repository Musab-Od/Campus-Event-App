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
import com.eventapp.model.Attendee;
import com.eventapp.dao.ReservationDAO;
import com.eventapp.dao.EventDAO;

@WebServlet("/manage-attendees")
public class ManageAttendeesServlet extends HttpServlet {

    private ReservationDAO reservationDAO = new ReservationDAO();
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
        if (!"ORGANIZER".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int eventId = Integer.parseInt(request.getParameter("id"));
        
        // Pass the event title and the list of attendees to the JSP
        request.setAttribute("event", eventDAO.getEventById(eventId, user.getId()));
        List<Attendee> attendees = reservationDAO.getAttendeesForEvent(eventId);
        request.setAttribute("attendees", attendees);
        
        request.getRequestDispatcher("/WEB-INF/views/manage-attendees.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        int eventId = Integer.parseInt(request.getParameter("eventId"));
        String[] studentIds = request.getParameterValues("studentIds");

        // Loop through all submitted students and save their status
        if (studentIds != null) {
            for (String idStr : studentIds) {
                int studentId = Integer.parseInt(idStr);
                // Grab the specific dropdown value for this student (e.g., status_5, status_12)
                String status = request.getParameter("status_" + studentId); 
                reservationDAO.updateAttendance(eventId, studentId, status);
            }
        }
        
        // Send them back to the dashboard with a success message
        response.sendRedirect(request.getContextPath() + "/dashboard?success=attendanceSaved");
    }
}