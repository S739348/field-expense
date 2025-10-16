package com.smarthome.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    User user;

    @Enumerated(EnumType.STRING)
    private SessionStatus session_status = SessionStatus.Active;

    @Column(nullable = false)
    private LocalDateTime start_time;

    private LocalDateTime end_time;

    @Column(nullable = false)
    private Double start_lat;

    @Column(nullable = false)
    private Double start_lon;

    private Double total_distance = 0.0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum SessionStatus {
        Active, Close, Not_Able_to_Connect
    }

    // Getters & Setters
    public Long getSession_id() { return sessionId; }
    public void setSession_id(Long session_id) { this.sessionId = session_id; }

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
}
