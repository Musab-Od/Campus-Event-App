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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    // Show the login form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    // Process the login credentials
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Use the DAO to check the database
        User loggedInUser = userDAO.authenticateUser(email, password);

        if (loggedInUser != null) {
            // SUCCESS! Create a session for the user
            HttpSession session = request.getSession();
            session.setAttribute("user", loggedInUser);
            
            // Redirect them to a dashboard (we will build this later)
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            // FAILED! Wrong password or blocked user
            request.setAttribute("errorMessage", "Invalid email or password.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}