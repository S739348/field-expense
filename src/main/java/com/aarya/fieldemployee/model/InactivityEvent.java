package com.aarya.fieldemployee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inactivity_event", indexes = {
        @Index(name = "idx_task_inactive_time", columnList = "task_id, start_time DESC")
})
public class InactivityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_inactive_device",
                    foreignKeyDefinition = "FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE"))
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",
            foreignKey = @ForeignKey(name = "fk_inactive_task",
                    foreignKeyDefinition = "FOREIGN KEY (task_id) REFERENCES task(task_id) ON DELETE CASCADE"))
    private Task task;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "avg_speed", precision = 5, scale = 2)
    private BigDecimal avgSpeed = BigDecimal.ZERO;

    @Column(name = "location_at_start_lat", precision = 10, scale = 8)
    private BigDecimal locationAtStartLat;

    @Column(name = "location_at_start_lng", precision = 11, scale = 8)
    private BigDecimal locationAtStartLng;

    @Column(name = "alert_sent")
    private Boolean alertSent = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 20, nullable = false)
    private EventType eventType;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EventType {
        TASK_STARTED, STOP, MOVE, TASK_END
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(BigDecimal avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public BigDecimal getLocationAtStartLat() {
        return locationAtStartLat;
    }

    public void setLocationAtStartLat(BigDecimal locationAtStartLat) {
        this.locationAtStartLat = locationAtStartLat;
    }

    public BigDecimal getLocationAtStartLng() {
        return locationAtStartLng;
    }

    public void setLocationAtStartLng(BigDecimal locationAtStartLng) {
        this.locationAtStartLng = locationAtStartLng;
    }

    public Boolean getAlertSent() {
        return alertSent;
    }

    public void setAlertSent(Boolean alertSent) {
        this.alertSent = alertSent;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
