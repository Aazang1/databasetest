package com.example.dao;

import com.example.entity.ScheduledTodoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScheduledTodoTaskRepository extends JpaRepository<ScheduledTodoTask, Long> {
    List<ScheduledTodoTask> findByActiveTrue();

    @Transactional
    @Modifying
    @Query("UPDATE ScheduledTodoTask t SET t.active = :active, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void updateActiveStatus(Long id, Boolean active);
    List<ScheduledTodoTask> findAllByUserId(String userId);
}
