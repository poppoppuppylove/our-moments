package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    Category findById(Long categoryId);
    List<Category> findAll();
    int insert(Category category);
    int update(Category category);
    int deleteById(Long categoryId);
}
