package com.example.service.impl;


import com.example.dao.TodoRepository;
import com.example.entity.Todo;
import com.example.entity.User;
import com.example.service.TodoService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<Todo> getAllTodosForCurrentUser(String username) {
        return todoRepository.findAllByUseridOrderByDueDateDesc(username);
    }

    @Override
    public Todo createTodo(Todo todo) {
        User user = userService.getCurrentUser(todo.getUserid());
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        todo.setCreatedAt(date);
        todo.setUpdatedAt(date);

        return todoRepository.save(todo);
    }

    @Override
    public Todo updateTodo(Long id, Todo todoDetails) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setTitle(todoDetails.getTitle());
        todo.setDescription(todoDetails.getDescription());
        todo.setStatus(todoDetails.getStatus());
        todo.setPriority(todoDetails.getPriority());
        todo.setDueDate(todoDetails.getDueDate());

        return todoRepository.save(todo);
    }

    @Override
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todoRepository.delete(todo);
    }


    @Transactional
    public void checkAndUpdateExpiredTodos() {
        Date now = new Date();
        // 查找所有截止日期已过且状态不是"已完成"的待办事项
        List<Todo> expiredTodos = todoRepository.findByDueDateBeforeAndStatusNot(now, "已完成");

        expiredTodos.forEach(todo -> {
            if (!"已过期".equals(todo.getStatus())) {
                todo.setStatus("已过期");
                todo.setUpdatedAt(new Date()); // 更新修改时间
            }
        });

        todoRepository.saveAll(expiredTodos); // 批量更新
    }
}
