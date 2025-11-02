package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.UUID;

public class ExpenseCreateRequest {

    @NotNull(message = "Task ID is required")
    private UUID taskId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;
    private MultipartFile receiptFile;

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
}
