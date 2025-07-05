package com.example.service;


import com.example.entity.User;

import java.util.List;

public interface UserService {
    User register(User user);
    User getCurrentUser(String username);
    String login(String username, String password);
    List<User> getAllUsers();
}
