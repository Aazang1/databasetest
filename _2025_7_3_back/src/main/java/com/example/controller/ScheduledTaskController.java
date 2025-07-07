package com.example.controller;

import com.example.entity.ScheduledTodoTask;
import com.example.service.ScheduledTodoTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduled-tasks")
public class ScheduledTaskController {

    @Autowired
    private ScheduledTodoTaskService taskService;

    @PostMapping
    public ResponseEntity<ScheduledTodoTask> createTask(@RequestBody ScheduledTodoTask task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping
    public ResponseEntity<List<ScheduledTodoTask>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduledTodoTask> getTask(@PathVariable Long id) {
        ScheduledTodoTask task = taskService.getTaskById(id);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduledTodoTask> updateTask(
            @PathVariable Long id,
            @RequestBody ScheduledTodoTask taskDetails) {
        ScheduledTodoTask updatedTask = taskService.updateTask(id, taskDetails);
        return updatedTask != null ? ResponseEntity.ok(updatedTask) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> toggleTaskStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        taskService.toggleTaskStatus(id, active);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<Void> executeTaskNow(@PathVariable Long id) {
        taskService.executeTaskNow(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
