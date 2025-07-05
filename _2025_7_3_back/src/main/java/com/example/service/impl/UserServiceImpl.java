package com.example.service.impl;

import com.example.dao.UserRepository;
import com.example.entity.User;

import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
//            throw new UsernameAlreadyExistsException();
        }

        if (userRepository.existsByEmail(user.getEmail())) {
//            throw new EmailAlreadyExistsException();
        }

        // 直接保存密码，不加密（仅适用于开发/测试环境）
        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }

//    @Override
//    public User getCurrentUser() {
////         直接从SecurityContext获取用户名
//        String username = SecurityContextHolder.getContext()
//                .getAuthentication().getName();
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }

    @Override
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "error+用户不存在";
        }

        if (!user.getPassword().equals(password)) {
            return "error";
        }
        else{
            return "success";
        }
    }


}
