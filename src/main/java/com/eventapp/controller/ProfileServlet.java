package com.eventapp.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.eventapp.model.User;
import com.eventapp.dao.UserDAO;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    // Show the Profile Form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    // Save the Updated Profile
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        // Grab the new values from the form
        String newName = request.getParameter("name");
        String newPassword = request.getParameter("password");
        String newFaculty = request.getParameter("faculty");
        String newDepartment = request.getParameter("department");
        int newAdmissionYear = Integer.parseInt(request.getParameter("admissionYear"));

        // Update our Java object
        currentUser.setName(newName);
        currentUser.setPassword(newPassword);
        currentUser.setFaculty(newFaculty);
        currentUser.setDepartment(newDepartment);
        currentUser.setAdmissionYear(newAdmissionYear);

        // Save to Database
        boolean isUpdated = userDAO.updateUser(currentUser);

        if (isUpdated) {
            // CRITICAL: Overwrite the old session object with the newly updated one!
            session.setAttribute("user", currentUser);
            response.sendRedirect(request.getContextPath() + "/profile?success=true");
        } else {
            response.sendRedirect(request.getContextPath() + "/profile?error=true");
        }
    }
}