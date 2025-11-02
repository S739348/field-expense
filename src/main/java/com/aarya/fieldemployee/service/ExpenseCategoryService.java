package com.aarya.fieldemployee.service;

import com.aarya.fieldemployee.dtorequest.ExpenseCategoryCreateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseCategoryDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.ExpenseCategoryResponse;
import java.util.List;
import java.util.UUID;

public interface ExpenseCategoryService {
    ExpenseCategoryResponse createCategory(ExpenseCategoryCreateRequest request, UUID requestingUserId);
    void deleteCategories(ExpenseCategoryDeleteRequest request, UUID requestingUserId);
    List<ExpenseCategoryResponse> showCategories();
}
