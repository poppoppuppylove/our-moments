package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.User;
import java.util.List;

public interface UserService {
    User getUserById(Long userId);
    User getUserByUsername(String username);
    User register(User user);
    User updateUser(Long userId, User user);
    void deleteUser(Long userId);
}
