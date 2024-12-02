package com.personalproject.taskmanagement.application;

import com.personalproject.taskmanagement.domain.Task;
import com.personalproject.taskmanagement.domain.TaskNotFoundException;
import com.personalproject.taskmanagement.dto.TaskDTO;
import com.personalproject.taskmanagement.infrastructure.TaskRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task addTask(TaskDTO taskDTO) {
        Task task = mapToEntity(taskDTO);
        task.setId(UUID.randomUUID().toString());
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateStatus(String id) {
        Task existingTask = getTaskById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        existingTask.setCompleted(true);
        return taskRepository.save(existingTask);
    }

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByStatus(boolean completed, Pageable pageable) {
        return taskRepository.findByCompleted(completed, pageable);
    }

    private Task mapToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setLabel(taskDTO.getLabel());
        task.setDescription(taskDTO.getDescription());
        return task;
    }
}