package com.smarthome.dto;

import com.smarthome.model.Expense;
import com.smarthome.model.Session;
import com.smarthome.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ManagerSessionDTO {

    private Long sessionId;
    private String sessionName;
    private Session.SessionStatus sessionStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startLat;
    private Double startLon;
    private Double totalDistance;
    private String notes;
    private String userName;
    private String userEmail;
    private User.Role userRole; // match type
    private Long userId;
    private List<Expense> expenses;

    public ManagerSessionDTO(Long sessionId, String sessionName, Session.SessionStatus sessionStatus,
                             LocalDateTime startTime, LocalDateTime endTime,
                             Double startLat, Double startLon, Double totalDistance,
                             String notes, String userName, String userEmail,
                             User.Role userRole, Long userId) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.sessionStatus = sessionStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLat = startLat;
        this.startLon = startLon;
        this.totalDistance = totalDistance;
        this.notes = notes;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.userId = userId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Session.SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(Session.SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLon() {
        return startLon;
    }

    public void setStartLon(Double startLon) {
        this.startLon = startLon;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public User.Role getUserRole() {
        return userRole;
    }

    public void setUserRole(User.Role userRole) {
        this.userRole = userRole;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
