<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Event - Campus Events</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
<a class="navbar-brand exit-link" id="backToDash" 
   href="${pageContext.request.contextPath}${sessionScope.user.role == 'ADMIN' ? '/admin-dashboard' : '/dashboard'}">
   ⬅ Back to Dashboard
</a>
    </div>
</nav>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-warning text-dark">
                    <h4 class="mb-0">Edit Event: ${event.title}</h4>
                </div>
                <div class="card-body">
                    <form id="editEventForm" action="${pageContext.request.contextPath}/edit-event" method="POST" enctype="multipart/form-data">
                        
                        <input type="hidden" name="id" value="${event.id}">

                        <div class="mb-3">
                            <label class="form-label">Event Title</label>
                            <input type="text" name="title" class="form-control" value="${event.title}" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea name="description" class="form-control" rows="3" required>${event.description}</textarea>
                        </div>

                        <div class="row mb-3">
                            <div class="col">
                                <label class="form-label">Department / Club</label>
                                <input type="text" name="departmentClub" class="form-control" value="${event.departmentClub}" required>
                            </div>
                            <div class="col">
                                <label class="form-label">Location (Room/Building)</label>
                                <input type="text" name="location" class="form-control" value="${event.location}" required>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col">
                                <label class="form-label">Date & Time</label>
                                <input type="datetime-local" name="eventDate" class="form-control" value="${event.eventDate}" required>
                            </div>
                            <div class="col">
                                <label class="form-label">Capacity (Locked)</label>
                                <input type="number" class="form-control bg-light" value="${event.capacity}" readonly>
                            </div>
                        </div>

                        <div class="row mb-4">
                            <div class="col">
                                <label class="form-label">Category</label>
                                <select name="category" class="form-select">
                                    <option value="Educational" ${event.category == 'Educational' ? 'selected' : ''}>Educational</option>
                                    <option value="Social" ${event.category == 'Social' ? 'selected' : ''}>Social</option>
                                    <option value="Sports" ${event.category == 'Sports' ? 'selected' : ''}>Sports</option>
                                    <option value="Cultural" ${event.category == 'Cultural' ? 'selected' : ''}>Cultural</option>
                                    <option value="Technical" ${event.category == 'Technical' ? 'selected' : ''}>Technical</option>
                                </select>
                            </div>
                            <div class="col">
                                <label class="form-label">Status</label>
                                <select name="status" class="form-select">
                                    <option value="OPEN" ${event.status == 'OPEN' ? 'selected' : ''}>OPEN</option>
                                    <option value="CLOSED" ${event.status == 'CLOSED' ? 'selected' : ''}>CLOSED</option>
                                    <option value="COMPLETED" ${event.status == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
        <label class="form-label">Event Poster (Max 5MB)</label>
        <input type="file" name="image" class="form-control" accept="image/*">
    </div>

                        <button type="submit" id="submitBtn" class="btn btn-warning w-100">Save Changes</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    let formChanged = false;
    const form = document.getElementById('editEventForm');
    
    // Track any changes in the form
    form.addEventListener('input', () => formChanged = true);

    // The Back button logic
    document.getElementById('backToDash').addEventListener('click', function(e) {
        if (formChanged) {
            const leave = confirm("Are you sure you want to leave? Your edited information will be lost.");
            if (!leave) {
                e.preventDefault(); // Stop the link from working
            }
        }
    });

    // Make sure we don't show the warning if they are actually submitting the form
    document.getElementById('submitBtn').addEventListener('click', () => formChanged = false);
</script>

</body>
</html>