package com.aarya.fieldemployee.dtorequest;

import com.aarya.fieldemployee.model.Expense;
import com.aarya.fieldemployee.model.Expense.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.UUID;

public class ExpenseUpdateRequest {
    @NotNull(message = "Expense ID is required")
    private UUID expenseId;
    private UUID taskId;
    private Long categoryId;
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    private String description;
    private MultipartFile receiptFile;
    private Status status;
    private Expense.PaymentStatus paymentStatus;
    public UUID getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(UUID expenseId) {
        this.expenseId = expenseId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public MultipartFile getReceiptFile() {
        return receiptFile;
    }

    public void setReceiptFile(MultipartFile receiptFile) {
        this.receiptFile = receiptFile;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Expense.PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Expense.PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
