package com.example.service;

import com.example.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TodoExpirationScheduler {
    @Autowired
    private TodoService todoService; // 移除final

    @Scheduled(cron = "0 * * * * ?")
    public void autoCheckExpiredTodos() {
        todoService.checkAndUpdateExpiredTodos();
    }
}
