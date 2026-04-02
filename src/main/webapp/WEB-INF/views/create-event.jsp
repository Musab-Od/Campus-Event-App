<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Event - Campus Events</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" id="backToDash" href="${pageContext.request.contextPath}/dashboard">⬅ Back to Dashboard</a>
        <span class="navbar-text text-white">
            Organizer: ${sessionScope.user.name}
        </span>
    </div>
</nav>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">Create a New Event</h4>
                </div>
                <div class="card-body">
                    
                    <% if(request.getAttribute("errorMessage") != null) { %>
                        <div class="alert alert-danger"><%= request.getAttribute("errorMessage") %></div>
                    <% } %>

                    <form id="createEventForm" action="${pageContext.request.contextPath}/create-event" method="POST" enctype="multipart/form-data">
                        
                        <div class="mb-3">
                            <label class="form-label">Event Title</label>
                            <input type="text" name="title" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea name="description" class="form-control" rows="3" required></textarea>
                        </div>

                        <div class="row mb-3">
                            <div class="col">
                                <label class="form-label">Department / Club</label>
                                <input type="text" name="departmentClub" class="form-control" required>
                            </div>
                            <div class="col">
                                <label class="form-label">Location (Room/Building)</label>
                                <input type="text" name="location" class="form-control" required>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col">
                                <label class="form-label">Date & Time</label>
                                <input type="datetime-local" name="eventDate" class="form-control" required>
                            </div>
                            <div class="col">
                                <label class="form-label">Capacity (Max Attendees)</label>
                                <input type="number" name="capacity" class="form-control" min="1" required>
                            </div>
                        </div>

                        <div class="row mb-4">
                            <div class="col">
                                <label class="form-label">Category</label>
                                <select name="category" class="form-select">
                                    <option value="Educational">Educational</option>
                                    <option value="Social">Social</option>
                                    <option value="Sports">Sports</option>
                                    <option value="Cultural">Cultural</option>
                                    <option value="Technical">Technical</option>
                                </select>
                            </div>
                            <div class="col">
                                <label class="form-label">Event Type (Factory Pattern)</label>
                                <select name="eventType" class="form-select">
                                    <option value="Workshop">Workshop</option>
                                    <option value="Seminar">Seminar</option>
                                    <option value="Club Social Event">Club Social Event</option>
                                    <option value="Sports Activity">Sports Activity</option>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
        <label class="form-label">Event Poster (Max 5MB)</label>
        <input type="file" name="image" class="form-control" accept="image/*">
    </div>

                        <button type="submit" id="submitBtn" class="btn btn-primary w-100">Create Event</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    let formChanged = false;
    const form = document.getElementById('createEventForm');
    
    form.addEventListener('input', () => formChanged = true);

    document.getElementById('backToDash').addEventListener('click', function(e) {
        if (formChanged) {
            if (!confirm("Are you sure you want to leave? Your information will be lost.")) {
                e.preventDefault();
            }
        }
    });

    document.getElementById('submitBtn').addEventListener('click', () => formChanged = false);
</script>

</body>
</html>