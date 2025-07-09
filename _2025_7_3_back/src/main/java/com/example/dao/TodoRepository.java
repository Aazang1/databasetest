package com.example.dao;

import com.example.entity.Todo;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByUseridOrderByDueDateDesc(String userid);


    List<Todo> findByDueDateBeforeAndStatusNot(Date dueDate, String status);

//    Todo findByid(Long id);


    // 可以根据需要添加更多查询方法
//    List<Todo> findByUseridAndStatus(String userid, Todo.TodoStatus status);
//
//    List<Todo> findByUseridAndPriority(String userid, Todo.TodoPriority priority);
}
