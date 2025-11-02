package com.aarya.fieldemployee.service.impl;

import com.aarya.fieldemployee.dtorequest.ExpenseCategoryCreateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseCategoryDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.ExpenseCategoryResponse;
import com.aarya.fieldemployee.model.Employee;
import com.aarya.fieldemployee.model.ExpenseCategory;
import com.aarya.fieldemployee.repository.EmployeeRepository;
import com.aarya.fieldemployee.repository.ExpenseCategoryRepository;
import com.aarya.fieldemployee.service.ExpenseCategoryService;
import com.aarya.fieldemployee.util.Authorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    @Autowired
    private ExpenseCategoryRepository categoryRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private Authorized authorized;

    @Override
    @Transactional
    public ExpenseCategoryResponse createCategory(ExpenseCategoryCreateRequest request, UUID requestingUserId) {
        Employee employee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getRole() != Employee.Role.Admin &&
                employee.getRole() != Employee.Role.Hr ) {
            throw new RuntimeException("Unauthorized to create expense categories");
        }

        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category name already exists: " + request.getName());
        }

        ExpenseCategory category = new ExpenseCategory();
        category.setName(request.getName());

        category = categoryRepository.save(category);
        return convertToResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategories(ExpenseCategoryDeleteRequest request, UUID requestingUserId) {
        Employee employee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getRole() != Employee.Role.Admin) {
            throw new RuntimeException("Only Admin can delete expense categories");
        }

        for (Long categoryId : request.getCategoryIds()) {
            ExpenseCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

            if (categoryRepository.hasAssociatedExpenses(categoryId)) {
                throw new RuntimeException("Cannot delete category with associated expenses: " + category.getName());
            }
        }

        categoryRepository.deleteAllById(request.getCategoryIds());
    }

    @Override
    public List<ExpenseCategoryResponse> showCategories() {
        List<ExpenseCategory> categories = categoryRepository.findAllByOrderByNameAsc();
        return categories.stream().map(this::convertToResponse).toList();
    }

    private ExpenseCategoryResponse convertToResponse(ExpenseCategory category) {
        ExpenseCategoryResponse response = new ExpenseCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}
