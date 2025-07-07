package com.example.service;


import com.example.dao.TodoRepository;
import com.example.entity.Todo;
import com.example.entity.ScheduledTodoTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class TodoTaskRunner {

    @Autowired
    private TodoRepository todoRepository;

    public void executeTask(ScheduledTodoTask task) {
        try {
            Todo todo = new Todo();
            todo.setTitle(task.getTodoTitle());
            todo.setDescription(task.getTodoDescription());
            todo.setStatus(task.getTodoStatus());
            todo.setPriority(task.getTodoPriority());

            // 计算到期时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, task.getDaysUntilDue());
            todo.setDueDate(calendar.getTime());

            todo.setCreatedAt(new Date());
            todo.setUpdatedAt(new Date());
            todo.setUserid(task.getUserId());

            todoRepository.save(todo);

        } catch (Exception e) {
            // 错误处理逻辑
            e.printStackTrace();
        }
    }
}
