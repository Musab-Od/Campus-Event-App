<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.eventapp.model.Event" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Dashboard - Campus Events</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Campus Events</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link active" href="${pageContext.request.contextPath}/dashboard">Browse Events</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/my-tickets">My Tickets</a>
                </li>
            </ul>
        </div>
        <div class="d-flex text-white align-items-center">
            <span class="me-3">Welcome, ${sessionScope.user.name} (Student)</span>
            <a href="${pageContext.request.contextPath}/profile" class="btn btn-sm btn-info me-2 text-white">Profile</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light">Logout</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    
    <% if("ticketBooked".equals(request.getParameter("success"))) { %>
        <div class="alert alert-success">Ticket successfully reserved! See you there.</div>
    <% } %>
    <% if("ticketCancelled".equals(request.getParameter("success"))) { %>
        <div class="alert alert-info">Your ticket reservation has been cancelled.</div>
    <% } %>
    <% if("soldOut".equals(request.getParameter("error"))) { %>
        <div class="alert alert-danger">Sorry, that event is completely sold out.</div>
    <% } %>
    <% if("alreadyBooked".equals(request.getParameter("error"))) { %>
        <div class="alert alert-warning">You have already reserved a ticket for this event!</div>
    <% } %>
    <% if("timeConflict".equals(request.getParameter("error"))) { %>
        <div class="alert alert-danger fw-bold">
            ⚠️ Booking Failed: You already have a ticket for another event happening at this exact same time!
        </div>
    <% } %>

    <div class="mb-4">
        <h2>Upcoming Campus Events</h2>
        <p class="text-muted">Browse and reserve your spot for workshops, seminars, and club activities.</p>
    </div>
    <div class="card shadow-sm mb-4 border-0 bg-white">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/dashboard" method="GET" class="row g-2">
            <div class="col-md-3">
                <select name="filterType" class="form-select">
                    <option value="" disabled selected>Select Filter...</option>
                    <option value="title">Search by Title</option>
                    <option value="category">Category (Educational, Social...)</option>
                    <option value="department">Department/Club</option>
                    <option value="type">Event Type (Workshop, Seminar...)</option>
                    <option value="date">Date (YYYY-MM-DD)</option>
                    <option value="availability">Show Available Only</option>
                </select>
            </div>
            <div class="col-md-7">
                <input type="text" name="query" class="form-control" placeholder="Type your search here...">
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-primary w-100">🔍 Search</button>
            </div>
        </form>
        <div class="mt-2 text-end">
            <a href="${pageContext.request.contextPath}/dashboard" class="text-decoration-none small text-muted">Clear Filters</a>
        </div>
    </div>
</div>
    <%
        List<Event> openEvents = (List<Event>) request.getAttribute("openEvents");
        Set<Integer> bookedIds = (Set<Integer>) request.getAttribute("bookedIds");
        
        if (openEvents == null || openEvents.isEmpty()) {
    %>
        <div class="card shadow-sm">
            <div class="card-body text-center p-5 text-muted">
                <h5>No upcoming events found.</h5>
                <p>Check back later for new activities!</p>
            </div>
        </div>
    <% } else { %>
        <div class="row">
        <% for(Event e : openEvents) { %>
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow-sm border-0 bg-white">
                    <div class="card-body">
                        <span class="badge bg-secondary mb-2"><%= e.getCategory() %></span>
                        <span class="badge bg-primary mb-2"><%= e.getEventType() %></span>
                        
                        <h5 class="card-title fw-bold"><%= e.getTitle() %></h5>
                        <p class="card-text text-muted small mb-2">
                            📅 <%= e.getEventDate().toString().replace("T", " ") %> <br>
                            📍 <%= e.getLocation() %>
                        </p>
                        <p class="card-text text-truncate"><%= e.getDescription() %></p>
                    </div>
                    <div class="card-footer bg-transparent border-top-0">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <small class="text-muted fw-bold">
                                Seats Left: <span class="text-primary"><%= e.getAvailableSeats() %></span>
                            </small>
                            <small class="text-muted">Hosted by: <%= e.getDepartmentClub() %></small>
                        </div>
                        
                        <% if (bookedIds != null && bookedIds.contains(e.getId())) { %>
                            <button class="btn btn-outline-success w-100 fw-bold" disabled>✅ Already Booked</button>
                        <% } else if (e.getAvailableSeats() > 0) { %>
                            <form action="${pageContext.request.contextPath}/book-ticket" method="POST">
                                <input type="hidden" name="eventId" value="<%= e.getId() %>">
                                <button type="submit" class="btn btn-success w-100">Book Ticket</button>
                            </form>
                        <% } else { %>
                            <button class="btn btn-secondary w-100" disabled>Sold Out</button>
                        <% } %>
                        
                    </div>
                </div>
            </div>
        <% } %>
        </div>
    <% } %>
</div>

</body>
</html>