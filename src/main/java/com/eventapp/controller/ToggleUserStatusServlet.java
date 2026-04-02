package com.eventapp.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.eventapp.dao.UserDAO;

@WebServlet("/toggle-user-status")
public class ToggleUserStatusServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int userId = Integer.parseInt(request.getParameter("userId"));
        boolean currentlyBlocked = Boolean.parseBoolean(request.getParameter("currentStatus"));
        
        UserDAO dao = new UserDAO();
        // If they were blocked, we unblock (false). If they were active, we block (true).
        dao.toggleBlockStatus(userId, !currentlyBlocked);
        
        response.sendRedirect(request.getContextPath() + "/admin-dashboard");
    }
}