package com.eventapp.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        // Get the session, but don't create a new one if it doesn't exist (false)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // This is the magic command that deletes the user from the server memory!
            session.invalidate(); 
        }
        
        // Redirect back to the login page with a success flag
        response.sendRedirect(request.getContextPath() + "/login?success=loggedout");
    }
}