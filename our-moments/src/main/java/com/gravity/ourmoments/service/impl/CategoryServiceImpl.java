package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.Category;
import com.gravity.ourmoments.mapper.CategoryMapper;
import com.gravity.ourmoments.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryMapper.findById(categoryId);
    }

    @Override
    public Category createCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        category.setCategoryId(categoryId);
        categoryMapper.update(category);
        return categoryMapper.findById(categoryId);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryMapper.deleteById(categoryId);
    }
}
