package com.aarya.fieldemployee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_ping", indexes = {
        @Index(name = "idx_task_time", columnList = "task_id, timestamp DESC"),
        @Index(name = "idx_device_time", columnList = "device_id, timestamp DESC"),
        @Index(name = "idx_location", columnList = "latitude, longitude")
})
public class LocationPing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ping_device",
                    foreignKeyDefinition = "FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE"))
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",
            foreignKey = @ForeignKey(name = "fk_ping_task",
                    foreignKeyDefinition = "FOREIGN KEY (task_id) REFERENCES task(task_id) ON DELETE CASCADE"))
    private Task task;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(precision = 8, scale = 2)
    private BigDecimal altitude;

    @Column(precision = 5, scale = 2)
    private BigDecimal speed = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal accuracy;

    @Column(precision = 3, scale = 1)
    private BigDecimal batteryLevel; // 0.0 - 100.0 %

    @Column
    private Integer networkSignal; // -120 to 0 (dBm RSSI)

    @Enumerated(EnumType.STRING)
    private AppState appState = AppState.Foreground;

    @Column(nullable = false)
    private LocalDateTime timestamp; // GPS timestamp

    @Column(name = "received_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime receivedAt = LocalDateTime.now();

    public  enum AppState{
        Foreground,Background,Kill
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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }

    public BigDecimal getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(BigDecimal batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Integer getNetworkSignal() {
        return networkSignal;
    }

    public void setNetworkSignal(Integer networkSignal) {
        this.networkSignal = networkSignal;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
