package com.eventapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.Event;
import com.eventapp.model.User;
import com.eventapp.dao.EventDAO;

@WebServlet("/edit-event")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB buffer
    maxFileSize = 1024 * 1024 * 5,       // 5 MB max per file
    maxRequestSize = 1024 * 1024 * 10    // 10 MB total request size
)
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
        int eventId = Integer.parseInt(request.getParameter("id"));
        
        EventDAO dao = new EventDAO();
        Event event = null;

        // SECURITY CHECK: VIP PASS FOR ADMINS
        if ("ADMIN".equals(user.getRole())) {
            // Admin can pull ANY event. We pass 0 as a dummy organizer ID and update the DAO
            event = dao.getEventByIdForAdmin(eventId); 
        } else if ("ORGANIZER".equals(user.getRole())) {
            // Organizer can only pull THEIR OWN event
            event = dao.getEventById(eventId, user.getId());
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Students cannot edit events.");
            return;
        }

        if (event == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found or you do not have permission.");
            return;
        }

        request.setAttribute("event", event);
        request.getRequestDispatcher("/WEB-INF/views/edit-event.jsp").forward(request, response);
    }

    // Process the updated data
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User organizer = (User) session.getAttribute("user");
        int eventId = Integer.parseInt(request.getParameter("id"));
        
        // 1. VIP FETCH: Admins use the Admin method, Organizers use the Organizer method
        Event updatedEvent = null;
        if ("ADMIN".equals(organizer.getRole())) {
            updatedEvent = eventDAO.getEventByIdForAdmin(eventId);
        } else {
            updatedEvent = eventDAO.getEventById(eventId, organizer.getId());
        }
        
        if (updatedEvent != null) {
            // 2. Handle Potential New File Upload
            jakarta.servlet.http.Part filePart = request.getPart("image");
            String fileName = filePart.getSubmittedFileName();

            // Only process the image if they actually uploaded a NEW one
            if (fileName != null && !fileName.isEmpty()) {
                String uniqueName = System.currentTimeMillis() + "_" + fileName;
                String appPath = request.getServletContext().getRealPath("");
                String savePath = appPath + java.io.File.separator + "uploads";
                
                java.io.File fileSaveDir = new java.io.File(savePath);
                if (!fileSaveDir.exists()) fileSaveDir.mkdir();

                filePart.write(savePath + java.io.File.separator + uniqueName);
                
                // Overwrite the old image URL with the new one
                updatedEvent.setImageUrl("uploads/" + uniqueName); 
            }

            // 3. Update the rest of the text fields
            updatedEvent.setTitle(request.getParameter("title"));
            updatedEvent.setDescription(request.getParameter("description"));
            updatedEvent.setDepartmentClub(request.getParameter("departmentClub"));
            updatedEvent.setLocation(request.getParameter("location"));
            updatedEvent.setCategory(request.getParameter("category"));
            updatedEvent.setStatus(request.getParameter("status"));
            
            String eventDateStr = request.getParameter("eventDate");
            updatedEvent.setEventDate(LocalDateTime.parse(eventDateStr));

            // 4. VIP SAVE AND REDIRECT (Only happens ONCE!)
            boolean isSaved = false;
            if ("ADMIN".equals(organizer.getRole())) {
                isSaved = eventDAO.updateEventForAdmin(updatedEvent);
            } else {
                isSaved = eventDAO.updateEvent(updatedEvent);
            }

            if (isSaved) {
                if ("ADMIN".equals(organizer.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/admin-dashboard?success=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard?success=updated");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?error=updateFailed");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found.");
        }
    }
}