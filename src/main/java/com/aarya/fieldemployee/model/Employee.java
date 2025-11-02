package com.aarya.fieldemployee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID employeeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private Long mobile;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role = Role.Field_Employee_Full_Time;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Active;

    @ManyToOne
    @JoinColumn(name = "manager_id", 
        foreignKey = @ForeignKey(name = "fk_employee_manager",
            foreignKeyDefinition = "FOREIGN KEY (manager_id) REFERENCES employee(employee_id) ON DELETE RESTRICT"))
    private Employee manager;

    @OneToMany(mappedBy = "manager")
    @JsonIgnore
    private List<Employee> subordinates;
   
    @OneToMany(mappedBy = "employee")
    private List<Device> devices;

    public enum Role {
        Admin, Manager, Hr, Finance, Field_Employee_Full_Time, Field_Employee_Vendor
    }

    public enum Status {
        Active, Inactive
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        this.subordinates = subordinates;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
