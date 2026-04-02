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
import com.eventapp.model.EventFactory;
import com.eventapp.model.User;
import com.eventapp.dao.EventDAO;

@WebServlet("/create-event")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB buffer
    maxFileSize = 1024 * 1024 * 5,       // 5 MB max per file
    maxRequestSize = 1024 * 1024 * 10    // 10 MB total request size
)
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
    
    HttpSession session = request.getSession();
    User organizer = (User) session.getAttribute("user");

    // 1. Handle File Upload FIRST
    jakarta.servlet.http.Part filePart = request.getPart("image"); 
    String fileName = filePart.getSubmittedFileName();
    String savedPath = "uploads/default.jpg"; // Fallback image

    if (fileName != null && !fileName.isEmpty()) {
        // Create a unique name to prevent overwriting
        String uniqueName = System.currentTimeMillis() + "_" + fileName;
        
        // Find where the project is running on your computer
        String appPath = request.getServletContext().getRealPath("");
        String savePath = appPath + java.io.File.separator + "uploads";
        
        // Create the folder if it's missing
        java.io.File fileSaveDir = new java.io.File(savePath);
        if (!fileSaveDir.exists()) fileSaveDir.mkdir();

        // Save the actual file to the hard drive
        filePart.write(savePath + java.io.File.separator + uniqueName);
        
        // This is the path we put in the DB
        savedPath = "uploads/" + uniqueName;
    }

    // 2. Grab the rest of the text fields
    String title = request.getParameter("title");
    String description = request.getParameter("description");
    String departmentClub = request.getParameter("departmentClub");
    String location = request.getParameter("location");
    int capacity = Integer.parseInt(request.getParameter("capacity"));
    String category = request.getParameter("category");
    String eventType = request.getParameter("eventType");
    LocalDateTime eventDate = LocalDateTime.parse(request.getParameter("eventDate"));

    try {
        Event newEvent = EventFactory.createEvent(eventType);
        newEvent.setOrganizerId(organizer.getId());
        newEvent.setTitle(title);
        newEvent.setDescription(description);
        newEvent.setDepartmentClub(departmentClub);
        newEvent.setLocation(location);
        newEvent.setCapacity(capacity);
        newEvent.setCategory(category);
        newEvent.setEventDate(eventDate);
        
        // 3. Set the Image URL
        newEvent.setImageUrl(savedPath); 

        if (eventDAO.createEvent(newEvent)) {
            response.sendRedirect(request.getContextPath() + "/dashboard?success=eventCreated");
        } else {
            request.setAttribute("errorMessage", "Database error.");
            request.getRequestDispatcher("/WEB-INF/views/create-event.jsp").forward(request, response);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}