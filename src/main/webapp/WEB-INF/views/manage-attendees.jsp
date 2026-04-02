<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.eventapp.model.Attendee" %>
<%@ page import="com.eventapp.model.Event" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Attendees</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Back to Dashboard</a>
    </div>
</nav>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card shadow-sm">
                <div class="card-header bg-info text-white d-flex justify-content-between align-items-center">
                    <h4 class="mb-0">Manage Attendance</h4>
                    <% Event event = (Event) request.getAttribute("event"); %>
                    <span class="badge bg-light text-dark"><%= event != null ? event.getTitle() : "Event" %></span>
                </div>
                
                <div class="card-body">
                    <% 
                        List<Attendee> attendees = (List<Attendee>) request.getAttribute("attendees");
                        if (attendees == null || attendees.isEmpty()) { 
                    %>
                        <div class="text-center p-4 text-muted">
                            <h5>No students have registered for this event yet.</h5>
                        </div>
                    <% } else { %>
                        <form action="${pageContext.request.contextPath}/manage-attendees" method="POST">
                            <input type="hidden" name="eventId" value="<%= request.getParameter("id") %>">
                            
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Student Name</th>
                                        <th>Email</th>
                                        <th>Attendance Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% for(Attendee a : attendees) { %>
                                        <tr>
                                            <td class="fw-bold"><%= a.getStudentName() %></td>
                                            <td class="text-muted"><%= a.getStudentEmail() %></td>
                                            <td>
                                                <input type="hidden" name="studentIds" value="<%= a.getStudentId() %>">
                                                
                                                <select name="status_<%= a.getStudentId() %>" class="form-select form-select-sm" style="width: 150px;">
                                                    <option value="PENDING" <%= "PENDING".equals(a.getAttendanceStatus()) ? "selected" : "" %>>Pending</option>
                                                    <option value="PRESENT" <%= "PRESENT".equals(a.getAttendanceStatus()) ? "selected" : "" %>>✅ Present</option>
                                                    <option value="ABSENT" <%= "ABSENT".equals(a.getAttendanceStatus()) ? "selected" : "" %>>❌ Absent</option>
                                                </select>
                                            </td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                            
                            <div class="text-end mt-4">
                                <button type="submit" class="btn btn-success">Save All Attendance</button>
                            </div>
                        </form>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>