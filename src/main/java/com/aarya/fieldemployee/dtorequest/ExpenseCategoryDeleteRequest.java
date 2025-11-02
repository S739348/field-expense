package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class ExpenseCategoryDeleteRequest {

    @NotEmpty(message = "Category IDs list cannot be empty")
    private List<Long> categoryIds;

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
