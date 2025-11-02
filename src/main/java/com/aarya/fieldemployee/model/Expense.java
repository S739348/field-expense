package com.aarya.fieldemployee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "expense", indexes = {
        @Index(name = "idx_task_expense", columnList = "task_id, status, created_at DESC")
})
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Task that this expense belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",
            foreignKey = @ForeignKey(name = "fk_expense_task",
                    foreignKeyDefinition = "FOREIGN KEY (task_id) REFERENCES task(task_id) ON DELETE CASCADE"))
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id",
            foreignKey = @ForeignKey(name = "fk_expense_employee",
                    foreignKeyDefinition = "FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE RESTRICT"))
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "fk_expense_category",
                    foreignKeyDefinition = "FOREIGN KEY (category_id) REFERENCES expense_category(id) ON DELETE RESTRICT"))
    private ExpenseCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "receipt_url", length = 255)
    private String receiptUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Pending; 

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by",
            foreignKey = @ForeignKey(name = "fk_expense_approver",
                    foreignKeyDefinition = "FOREIGN KEY (approved_by) REFERENCES employee(employee_id) ON DELETE SET NULL"))
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt ;
     @Column(name= "payment_status")
     private  PaymentStatus paymentStatus=PaymentStatus.Pending;

    public  enum Status{
       Pending,Approved,Rejected
    }
    public  enum PaymentStatus{
        Pending,Processing,Paid
    }


    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Employee getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Employee approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
