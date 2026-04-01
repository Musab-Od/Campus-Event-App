<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - Campus Events</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white text-center">
                    <h4>Create an Account</h4>
                </div>
                <div class="card-body">
                    
                    <%-- Show error message if registration fails --%>
                    <% if(request.getAttribute("errorMessage") != null) { %>
                        <div class="alert alert-danger"><%= request.getAttribute("errorMessage") %></div>
                    <% } %>

                    <%-- The Form: Action points to our Servlet's @WebServlet URL --%>
                    <form action="${pageContext.request.contextPath}/register" method="POST">
                        
                        <div class="mb-3">
                            <label class="form-label">Full Name</label>
                            <input type="text" name="name" class="form-control" required>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Email Address</label>
                            <input type="email" name="email" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Password</label>
                            <input type="password" name="password" class="form-control" required>
                        </div>

                        <div class="row mb-3">
                            <div class="col">
                                <label class="form-label">Faculty</label>
                                <input type="text" name="faculty" class="form-control" required>
                            </div>
                            <div class="col">
                                <label class="form-label">Department</label>
                                <input type="text" name="department" class="form-control" required>
                            </div>
                        </div>

                        <div class="row mb-4">
                            <div class="col">
                                <label class="form-label">Admission Year</label>
                                <input type="number" name="admissionYear" class="form-control" min="2018" max="2026" required>
                            </div>
                            <div class="col">
                                <label class="form-label">Account Type</label>
                                <select name="role" class="form-select">
                                    <option value="STUDENT">Student</option>
                                    <option value="ORGANIZER">Event Organizer</option>
                                </select>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100">Register</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>