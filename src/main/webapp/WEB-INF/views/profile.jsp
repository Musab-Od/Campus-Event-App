<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Profile - Campus Events</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">⬅ Back to Dashboard</a>
        <div class="text-white">
            <span class="badge bg-primary">${sessionScope.user.role}</span>
        </div>
    </div>
</nav>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            
            <% if("true".equals(request.getParameter("success"))) { %>
                <div class="alert alert-success">Profile updated successfully!</div>
            <% } %>
            <% if("true".equals(request.getParameter("error"))) { %>
                <div class="alert alert-danger">Error updating profile. Please try again.</div>
            <% } %>

            <div class="card shadow-sm border-0">
                <div class="card-header bg-info text-white">
                    <h4 class="mb-0">My Profile</h4>
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/profile" method="POST">
                        
                        <div class="mb-3">
                            <label class="form-label text-muted small fw-bold">Full Name</label>
                            <input type="text" name="name" class="form-control" value="${sessionScope.user.name}" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label text-muted small fw-bold">Email Address (Cannot be changed)</label>
                            <input type="email" class="form-control bg-light" value="${sessionScope.user.email}" readonly>
                        </div>

                        <div class="mb-3">
                            <label class="form-label text-muted small fw-bold">Password</label>
                            <input type="text" name="password" class="form-control" value="${sessionScope.user.password}" required>
                            <div class="form-text">Your current password is shown. Change it to update.</div>
                        </div>

                        <div class="row mb-3">
                            <div class="col">
                                <label class="form-label text-muted small fw-bold">Faculty</label>
                                <input type="text" name="faculty" class="form-control" value="${sessionScope.user.faculty}" required>
                            </div>
                            <div class="col">
                                <label class="form-label text-muted small fw-bold">Department</label>
                                <input type="text" name="department" class="form-control" value="${sessionScope.user.department}" required>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label text-muted small fw-bold">Admission Year</label>
                            <input type="number" name="admissionYear" class="form-control" value="${sessionScope.user.admissionYear}" required>
                        </div>

                        <button type="submit" class="btn btn-info text-white w-100 fw-bold">Save Changes</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>