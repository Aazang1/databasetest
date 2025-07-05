package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
       String x= userService.login(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(x);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }


//    @GetMapping("/current")
//    public ResponseEntity<User> getCurrentUser() {
//        User user = userService.getCurrentUser();
//        return ResponseEntity.ok(user);
//    }

    @GetMapping("/hello")
    public String hello(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth details: " + auth); // 查看Principal和权限
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return "Hello1," + authentication.getName() + "!";
    }
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        List<User> users=userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}
