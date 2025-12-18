package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long categoryId);
    Category createCategory(Category category);
    Category updateCategory(Long categoryId, Category category);
    void deleteCategory(Long categoryId);
}
