package com.gravity.ourmoments.service;

import com.gravity.ourmoments.entity.User;
import java.util.List;

public interface UserService {
    User getUserById(Long userId);
    User getUserByUsername(String username);
    User register(User user);
    User updateUser(Long userId, User user);
    void deleteUser(Long userId);

    // Admin support
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);
    User updateUserRole(Long userId, String role);
    User resetPassword(Long userId, String newPassword);
}
