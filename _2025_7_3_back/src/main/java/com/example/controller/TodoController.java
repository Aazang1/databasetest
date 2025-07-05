package com.example.controller;


import com.example.entity.Todo;
import com.example.service.TodoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(@RequestParam String username) {
        System.out.println(username);
        List<Todo> todos = todoService.getAllTodosForCurrentUser(username);
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) throws JsonProcessingException {

        Todo createdTodo = todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        Todo updatedTodo = todoService.updateTodo(id, todo);
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
