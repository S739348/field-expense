package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public class ExpenseDeleteRequest {

    @NotEmpty(message = "Expense IDs list cannot be empty")
    private List<UUID> expenseIds;

    public List<UUID> getExpenseIds() {
        return expenseIds;
    }

    public void setExpenseIds(List<UUID> expenseIds) {
        this.expenseIds = expenseIds;
    }
}
