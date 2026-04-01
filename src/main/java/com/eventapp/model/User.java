package com.eventapp.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String faculty;
    private String department;
    private int admissionYear;
    private String role; // STUDENT, ORGANIZER, or ADMIN

    // Empty Constructor
    public User() {}

    // Full Constructor for Registration
    public User(String name, String email, String password, String faculty, String department, int admissionYear, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.faculty = faculty;
        this.department = department;
        this.admissionYear = admissionYear;
        this.role = role;
    }

    // Constructor with ID (for pulling from database)
    public User(int id, String name, String email, String password, String faculty, String department, int admissionYear, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.faculty = faculty;
        this.department = department;
        this.admissionYear = admissionYear;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getAdmissionYear() {
        return admissionYear;
    }

    public void setAdmissionYear(int admissionYear) {
        this.admissionYear = admissionYear;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}