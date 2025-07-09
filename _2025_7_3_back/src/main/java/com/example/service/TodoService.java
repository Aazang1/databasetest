package com.example.service;

import com.example.entity.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
public interface TodoService {
    List<Todo> getAllTodosForCurrentUser(String username);
    Todo createTodo(Todo todo);
    Todo updateTodo(Long id, Todo todo);
    void deleteTodo(Long id);
     void checkAndUpdateExpiredTodos();
}
