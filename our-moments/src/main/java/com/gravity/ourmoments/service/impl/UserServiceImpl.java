package com.gravity.ourmoments.service.impl;

import com.gravity.ourmoments.entity.User;
import com.gravity.ourmoments.mapper.UserMapper;
import com.gravity.ourmoments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User register(User user) {
        // In a real app, encrypt password here
        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        user.setUserId(userId);
        userMapper.update(user);
        return userMapper.findById(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userMapper.findByRole(role);
    }

    @Override
    public User updateUserRole(Long userId, String role) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setRole(role);
            userMapper.update(user);
            return userMapper.findById(userId);
        }
        return null;
    }
}
