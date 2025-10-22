package com.smarthome.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(nullable = false)
    private String session_name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"sessions", "expenses", "password"})
    private User user;

    @Enumerated(EnumType.STRING)
    private SessionStatus session_status = SessionStatus.Active;

    @Column(nullable = false)
    private LocalDateTime start_time;

    private LocalDateTime end_time;

    @Column(nullable = false)
    private Double start_lat;

    @Column(nullable = false)
    private Double start_lon;
    
    // Current location for real-time tracking (transient fields)
    @Transient
    private Double current_lat;
    
    @Transient
    private Double current_lon;

    private Double total_distance = 0.0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"session", "user", "category"})
    private List<Expense> expenses;

    public enum SessionStatus {
        Active, Close, Not_Able_to_Connect
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getSession_name() { return session_name; }
    public void setSession_name(String session_name) { this.session_name = session_name; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SessionStatus getSession_status() { return session_status; }
    public void setSession_status(SessionStatus session_status) { this.session_status = session_status; }

    public LocalDateTime getStart_time() { return start_time; }
    public void setStart_time(LocalDateTime start_time) { this.start_time = start_time; }

    public LocalDateTime getEnd_time() { return end_time; }
    public void setEnd_time(LocalDateTime end_time) { this.end_time = end_time; }

    public Double getStart_lat() { return start_lat; }
    public void setStart_lat(Double start_lat) { this.start_lat = start_lat; }

    public Double getStart_lon() { return start_lon; }
    public void setStart_lon(Double start_lon) { this.start_lon = start_lon; }

    public Double getTotal_distance() { return total_distance; }
    public void setTotal_distance(Double total_distance) { this.total_distance = total_distance; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }

    // Additional methods for real-time tracking
    public String getSessionName() { return session_name; }
    public void setSessionName(String sessionName) { this.session_name = sessionName; }
    
    public Double getLatitude() { return start_lat; }
    public void setLatitude(Double latitude) { this.start_lat = latitude; }
    
    public Double getLongitude() { return start_lon; }
    public void setLongitude(Double longitude) { this.start_lon = longitude; }
    
    public String getStatus() { 
        return session_status != null ? session_status.name() : "INACTIVE"; 
    }
    
    public void setStatus(String status) {
        try {
            this.session_status = SessionStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            this.session_status = SessionStatus.Not_Able_to_Connect;
        }
    }
}
