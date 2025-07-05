package com.example.service;


import com.example.entity.User;

public interface UserService {
    User register(User user);
    User getCurrentUser(String username);
    String login(String username, String password);
}
