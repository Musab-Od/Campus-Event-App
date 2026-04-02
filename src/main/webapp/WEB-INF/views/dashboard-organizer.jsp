<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.eventapp.model.Event" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Organizer Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="#">Campus Events</a>
        <div class="d-flex text-white align-items-center">
            <span class="me-3">Welcome, ${sessionScope.user.name} (Organizer)</span>
            <a href="${pageContext.request.contextPath}/profile" class="btn btn-sm btn-info me-2 text-white">Profile</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light">Logout</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    
    <% if("eventCreated".equals(request.getParameter("success"))) { %>
        <div class="alert alert-success">Event successfully created!</div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>My Hosted Events</h2>
        <a href="${pageContext.request.contextPath}/create-event" class="btn btn-primary">+ Create New Event</a>
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
        List<Event> myEvents = (List<Event>) request.getAttribute("myEvents");
        if (myEvents == null || myEvents.isEmpty()) {
    %>
        <div class="card shadow-sm">
            <div class="card-body text-center p-5 text-muted">
                <h5>You haven't created any events yet.</h5>
                <p>Click the button above to get started.</p>
            </div>
        </div>
    <% } else { %>
        <div class="row">
        <% for(Event e : myEvents) { %>
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow-sm border-0 bg-white">
                    <div class="card-body">
                        <span class="badge bg-primary mb-2"><%= e.getEventType() %></span>
                        
                        <h5 class="card-title fw-bold"><%= e.getTitle() %></h5>
                        <p class="card-text text-muted small mb-2">
                            📅 <%= e.getEventDate().toString().replace("T", " ") %> <br>
                            📍 <%= e.getLocation() %>
                        </p>
                        <p class="card-text text-truncate"><%= e.getDescription() %></p>
                    </div>
                    
                    <div class="card-footer bg-transparent border-top-0 d-flex flex-column gap-2">
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted fw-bold">
                                Attendees: <%= (e.getCapacity() - e.getAvailableSeats()) %> / <%= e.getCapacity() %>
                            </small>
                            <span class="badge bg-success"><%= e.getStatus() %></span>
                        </div>
        
                        <div class="d-flex justify-content-between mt-2">
                            <a href="${pageContext.request.contextPath}/manage-attendees?id=<%= e.getId() %>" 
                               class="btn btn-sm btn-info text-white">
                               👥 Attendees
                            </a>
                            
                            <div class="btn-group">
                                <a href="${pageContext.request.contextPath}/edit-event?id=<%= e.getId() %>" 
                                   class="btn btn-sm btn-outline-primary">✏️ Edit</a>
                                <a href="${pageContext.request.contextPath}/delete-event?id=<%= e.getId() %>" 
                                   class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('Are you sure you want to permanently delete this event?');">🗑️ Delete</a>
                            </div>
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