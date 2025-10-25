package com.smarthome.dto;

import com.smarthome.model.Expense;
import com.smarthome.model.Session;
import com.smarthome.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class SessionResponseDTO {

    private Long sessionId;
    private String sessionName;
    private Session.SessionStatus sessionStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startLat;
    private Double startLon;
    private Double totalDistance;
    private String notes;
    private List<Expense> expenses; // or whatever type s.expenses is
    private String managerName;
    private String mangerEmail;          // must match s.user.manager type

    public SessionResponseDTO(Long sessionId, String sessionName, Session.SessionStatus sessionStatus, LocalDateTime startTime, LocalDateTime endTime, Double startLat, Double startLon, Double totalDistance, String notes, String managerName, String mangerEmail) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.sessionStatus = sessionStatus;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLat = startLat;
        this.startLon = startLon;
        this.totalDistance = totalDistance;
        this.notes = notes;
        this.managerName = managerName;
        this.mangerEmail = mangerEmail;
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

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getMangerEmail() {
        return mangerEmail;
    }

    public void setMangerEmail(String mangerEmail) {
        this.mangerEmail = mangerEmail;
    }
}
