package com.personalproject.taskmanagement.presentation;

import com.personalproject.taskmanagement.application.TaskService;
import com.personalproject.taskmanagement.domain.Task;
import com.personalproject.taskmanagement.domain.TaskNotFoundException;
import com.personalproject.taskmanagement.dto.TaskDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task Management", description = "API for managing tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @PostMapping
    @Operation(summary = "Add a new task")
    public ResponseEntity<Task> addTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task createdTask = taskService.addTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update a task's status",
        description = "Marks a task as completed based on its ID"
    )
    public ResponseEntity<Void> updateTask(@PathVariable String id) {
        taskService.updateStatus(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Operation(summary = "Get paginated tasks with optional status filter")
    public ResponseEntity<Page<Task>> getAllTasks(
        @RequestParam(required = false) String status,
        @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        
        Page<Task> tasks;
        if (status != null && !status.equals("all")) {
            boolean completed = status.equals("completed");
            tasks = taskService.getTasksByStatus(completed, pageable);
        } else {
            tasks = taskService.getAllTasks(pageable);
        }
        
        return ResponseEntity.ok(tasks);
    }
}