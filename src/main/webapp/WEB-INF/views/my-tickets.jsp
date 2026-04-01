<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.eventapp.model.Event" %>
<%@ page import="java.time.LocalDateTime" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Tickets - Campus Events</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Campus Events</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Browse Events</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/my-tickets">My Tickets</a>
                </li>
            </ul>
        </div>
        <div class="d-flex text-white align-items-center">
            <span class="me-3">${sessionScope.user.name} (Student)</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light">Logout</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    
    <div class="mb-4">
        <h2>My Ticket Reservations</h2>
        <p class="text-muted">Manage your upcoming event attendance here.</p>
    </div>

    <%
        List<Event> myTickets = (List<Event>) request.getAttribute("myTickets");
        if (myTickets == null || myTickets.isEmpty()) {
    %>
        <div class="card shadow-sm">
            <div class="card-body text-center p-5 text-muted">
                <h5>You haven't reserved any tickets yet.</h5>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-3">Browse Upcoming Events</a>
            </div>
        </div>
    <% } else { %>
        <div class="row">
        <% 
            LocalDateTime now = LocalDateTime.now();
            for(Event e : myTickets) { 
                // We check the time in the UI to decide whether to show the Cancel button!
                boolean canCancel = e.getEventDate().isAfter(now);
        %>
            <div class="col-md-6 mb-4">
                <div class="card shadow-sm border-0 bg-white">
                    <div class="card-body">
                        <div class="d-flex justify-content-between">
                            <h5 class="card-title fw-bold"><%= e.getTitle() %></h5>
                            <span class="badge bg-success h-50">TICKET CONFIRMED</span>
                        </div>
                        <p class="card-text text-muted mb-2">
                            📅 <%= e.getEventDate().toString().replace("T", " ") %> <br>
                            📍 <%= e.getLocation() %>
                        </p>
                        
                        <div class="mt-4 pt-3 border-top d-flex justify-content-between align-items-center">
                            <small class="text-muted">ID: #EVT-<%= e.getId() %></small>
                            
                            <% if(canCancel) { %>
                                <form action="${pageContext.request.contextPath}/cancel-ticket" method="POST" class="m-0">
                                    <input type="hidden" name="eventId" value="<%= e.getId() %>">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Are you sure you want to cancel your ticket? This will release your seat.');">
                                        Cancel Reservation
                                    </button>
                                </form>
                            <% } else { %>
                                <span class="text-muted fst-italic small">Event has started (Cannot cancel)</span>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        <% } %>
        </div>
    <% } %>
</div>

</body>
</html>