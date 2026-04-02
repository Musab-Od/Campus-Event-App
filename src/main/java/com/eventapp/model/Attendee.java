package com.eventapp.model;

public class Attendee {
    private int studentId;
    private String studentName;
    private String studentEmail;
    private String attendanceStatus; // 'PENDING', 'PRESENT', or 'ABSENT'

    // Constructor
    public Attendee(int studentId, String studentName, String studentEmail, String attendanceStatus) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.attendanceStatus = attendanceStatus;
    }

    // Getters
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getAttendanceStatus() { return attendanceStatus; }
}