package com.eventapp.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.eventapp.model.User;
import com.eventapp.dao.UserDAO;

// This annotation magically routes the URL /register to this class!
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();

    // 1. Handle GET request (Show the webpage)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward the user to the JSP page to see the form
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    // 2. Handle POST request (Process the submitted form)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Grab all the data the user typed into the HTML form
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String faculty = request.getParameter("faculty");
        String department = request.getParameter("department");
        int admissionYear = Integer.parseInt(request.getParameter("admissionYear"));
        String role = request.getParameter("role"); // STUDENT or ORGANIZER

        // Create the User object
        User newUser = new User(name, email, password, faculty, department, admissionYear, role);
        
        // Save to Database using the DAO we made earlier
        boolean isRegistered = userDAO.registerUser(newUser);

        if (isRegistered) {
            // Success! Send them to the login page
            response.sendRedirect(request.getContextPath() + "/login?success=registered");
        } else {
            // Failed! (Probably a duplicate email). Send them back to the form with an error.
            request.setAttribute("errorMessage", "Registration failed. That email might already be taken.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}