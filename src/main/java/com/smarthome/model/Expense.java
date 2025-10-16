package com.smarthome.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expense_id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Session session;

    @ManyToOne
    private ExpenseCategories category;

    // Approver references
    @ManyToOne
    private User preApprover; // manager

    @ManyToOne
    private User hrApprover;

    @ManyToOne
    private User approver; // finance

    private BigDecimal amount;

    private String description;

    // Multi-level approval statuses (store as simple strings for flexibility)
    private String managerStatus;
    private String hrStatus;
    private String financeStatus;

    public enum PaymentStatus { PENDING, PAID, FAILED }

    @Enumerated(EnumType.STRING)
    private PaymentStatus payment_status = PaymentStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and setters
    public Long getExpense_id() { return expense_id; }
    public void setExpense_id(Long expense_id) { this.expense_id = expense_id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public ExpenseCategories getCategory() { return category; }
    public void setCategory(ExpenseCategories category) { this.category = category; }

    public User getPreApprover() { return preApprover; }
    public void setPreApprover(User preApprover) { this.preApprover = preApprover; }

    public User getHrApprover() { return hrApprover; }
    public void setHrApprover(User hrApprover) { this.hrApprover = hrApprover; }

    public User getApprover() { return approver; }
    public void setApprover(User approver) { this.approver = approver; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getManagerStatus() { return managerStatus; }
    public void setManagerStatus(String managerStatus) { this.managerStatus = managerStatus; }

    public String getHrStatus() { return hrStatus; }
    public void setHrStatus(String hrStatus) { this.hrStatus = hrStatus; }

    public String getFinanceStatus() { return financeStatus; }
    public void setFinanceStatus(String financeStatus) { this.financeStatus = financeStatus; }

    public PaymentStatus getPayment_status() { return payment_status; }
    public void setPayment_status(PaymentStatus payment_status) { this.payment_status = payment_status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
