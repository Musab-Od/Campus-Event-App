<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.eventapp.model.User, com.eventapp.model.Event" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Panel - Campus Events</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-danger shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="#">🛡️ ADMIN CONTROL PANEL</a>
        <div class="text-white">
            <span class="me-3">Logged in as: <strong>${user.name}</strong></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light">Logout</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow-sm border-0">
        <div class="card-header bg-white">
            <ul class="nav nav-tabs card-header-tabs" id="adminTab" role="tablist">
                <li class="nav-item">
                    <button class="nav-link active" id="users-tab" data-bs-toggle="tab" data-bs-target="#users" type="button">Manage Users</button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" id="events-tab" data-bs-toggle="tab" data-bs-target="#events" type="button">Manage Events</button>
                </li>
            </ul>
        </div>
        <div class="card-body p-4 tab-content" id="adminTabContent">
            
            <div class="tab-pane fade show active" id="users" role="tabpanel">
                <h4 class="mb-4">System Users</h4>
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<User> userList = (List<User>) request.getAttribute("users");
                           for(User u : userList) { %>
                        <tr>
                            <td>#<%= u.getId() %></td>
                            <td><%= u.getName() %></td>
                            <td><%= u.getEmail() %></td>
                            <td><span class="badge bg-secondary"><%= u.getRole() %></span></td>
                            <td>
                                <% if(u.isBlocked()) { %>
                                    <span class="badge bg-danger">BLOCKED</span>
                                <% } else { %>
                                    <span class="badge bg-success">ACTIVE</span>
                                <% } %>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/toggle-user-status" method="POST" class="d-inline">
                                    <input type="hidden" name="userId" value="<%= u.getId() %>">
                                    <input type="hidden" name="currentStatus" value="<%= u.isBlocked() %>">
                                    <% if(u.isBlocked()) { %>
                                        <button type="submit" class="btn btn-sm btn-outline-success">Unblock</button>
                                    <% } else { %>
                                        <button type="submit" class="btn btn-sm btn-outline-danger">Block</button>
                                    <% } %>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <div class="tab-pane fade" id="events" role="tabpanel">
                <h4 class="mb-4">All Campus Events</h4>
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Organizer ID</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Event> eventList = (List<Event>) request.getAttribute("events");
                           for(Event e : eventList) { %>
                        <tr>
                            <td>#<%= e.getId() %></td>
                            <td><strong><%= e.getTitle() %></strong></td>
                            <td>User #<%= e.getOrganizerId() %></td>
                            <td><span class="badge bg-info text-dark"><%= e.getStatus() %></span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/edit-event?id=<%= e.getId() %>&adminMode=true" class="btn btn-sm btn-primary">Edit</a>
                                <a href="${pageContext.request.contextPath}/delete-event?id=<%= e.getId() %>&adminMode=true" 
                                   class="btn btn-sm btn-danger" 
                                   onclick="return confirm('ADMIN: Delete this event permanently?')">Delete</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>