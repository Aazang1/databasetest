package com.example.service;


import com.example.dao.ScheduledTodoTaskRepository;
import com.example.entity.ScheduledTodoTask;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class ScheduledTodoTaskService {

    @Autowired
    private ScheduledTodoTaskRepository taskRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private TodoTaskRunner todoTaskRunner;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @PostConstruct
    public void init() {
        // 应用启动时加载所有active的任务
        List<ScheduledTodoTask> activeTasks = taskRepository.findByActiveTrue();
        activeTasks.forEach(this::scheduleTask);
    }

    public ScheduledTodoTask createTask(ScheduledTodoTask task) {
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());
        ScheduledTodoTask savedTask = taskRepository.save(task);

        if (savedTask.getActive()) {
            scheduleTask(savedTask);
        }

        return savedTask;
    }

    public List<ScheduledTodoTask> getAllTasks(String userid) {
        return taskRepository.findAllByUserId(userid);
    }

    public ScheduledTodoTask getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Transactional
    public ScheduledTodoTask updateTask(Long id, ScheduledTodoTask taskDetails) {
        ScheduledTodoTask task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            // 取消现有调度
            cancelScheduledTask(id);

            // 更新任务属性
            task.setTaskName(taskDetails.getTaskName());
            task.setCronExpression(taskDetails.getCronExpression());
            task.setTodoTitle(taskDetails.getTodoTitle());
            task.setTodoDescription(taskDetails.getTodoDescription());
            task.setTodoStatus(taskDetails.getTodoStatus());
            task.setTodoPriority(taskDetails.getTodoPriority());
            task.setDaysUntilDue(taskDetails.getDaysUntilDue());
            task.setActive(taskDetails.getActive());
            task.setUpdatedAt(new Date());

            ScheduledTodoTask savedTask = taskRepository.save(task);

            // 重新调度
            if (savedTask.getActive()) {
                scheduleTask(savedTask);
            }

            return savedTask;
        }
        return null;
    }

    @Transactional
    public void toggleTaskStatus(Long id, Boolean active) {
        taskRepository.updateActiveStatus(id, active);
        ScheduledTodoTask task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            if (active) {
                scheduleTask(task);
            } else {
                cancelScheduledTask(id);
            }
        }
    }

    @Transactional
    public void deleteTask(Long id) {
        cancelScheduledTask(id);
        taskRepository.deleteById(id);
    }

    public void executeTaskNow(Long taskId) {
        ScheduledTodoTask task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            todoTaskRunner.executeTask(task);
        }
    }

    private void scheduleTask(ScheduledTodoTask task) {
        Runnable taskRunner = () -> todoTaskRunner.executeTask(task);
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(
                taskRunner,
                new CronTrigger(task.getCronExpression())
        );

        scheduledTasks.put(task.getId(), scheduledFuture);
    }

    private void cancelScheduledTask(Long taskId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(taskId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledTasks.remove(taskId);
        }
    }
}
