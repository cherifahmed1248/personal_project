package com.personalproject.taskmanagement.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.personalproject.taskmanagement.domain.Task;

public interface TaskRepository extends JpaRepository<Task, String> {
    Page<Task> findByCompleted(boolean completed, Pageable pageable);
}