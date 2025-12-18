package com.gravity.ourmoments.mapper;

import com.gravity.ourmoments.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long userId);
    User findByUsername(String username);
    int insert(User user);
    int update(User user);
    int deleteById(Long userId);
}
