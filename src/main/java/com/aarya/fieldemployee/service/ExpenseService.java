package com.aarya.fieldemployee.service;

import com.aarya.fieldemployee.dtorequest.ExpenseCreateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseUpdateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.ExpenseResponse;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseCreateRequest request, UUID requestingUserId);
    ExpenseResponse updateExpense(ExpenseUpdateRequest request, UUID requestingUserId);
    void deleteExpenses(ExpenseDeleteRequest request, UUID requestingUserId);
    List<ExpenseResponse> showExpenses(UUID requestingUserId, String range);
}
