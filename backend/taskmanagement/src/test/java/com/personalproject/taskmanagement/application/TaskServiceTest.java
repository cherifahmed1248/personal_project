package com.personalproject.taskmanagement.application;

import com.personalproject.taskmanagement.domain.Task;
import com.personalproject.taskmanagement.domain.TaskNotFoundException;
import com.personalproject.taskmanagement.dto.TaskDTO;
import com.personalproject.taskmanagement.infrastructure.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task();
        task1.setId("1");
        task1.setLabel("Task 1");
        task1.setDescription("Description 1");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setId("2");
        task2.setLabel("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTaskById() {
        Task task = new Task();
        task.setId("1");
        task.setLabel("Task 1");
        task.setDescription("Description 1");
        task.setCompleted(false);

        when(taskRepository.findById("1")).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.getTaskById("1");
        assertTrue(foundTask.isPresent());
        assertEquals("Task 1", foundTask.get().getLabel());
        verify(taskRepository, times(1)).findById("1");
    }

    @Test
    void testAddTask() {
        TaskDTO task = new TaskDTO();
        task.setLabel("Task 1");
        task.setDescription("Description 1");
        taskService.addTask(task);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateStatus() {
        Task task = new Task();
        task.setId("1");
        task.setLabel("Task 1");
        task.setDescription("Description 1");
        task.setCompleted(false);

        when(taskRepository.findById("1")).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateStatus("1");
        assertTrue(updatedTask.isCompleted());
        verify(taskRepository, times(1)).findById("1");
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testUpdateStatusTaskNotFound() {
        when(taskRepository.findById("1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateStatus("1");
        });

        assertEquals("Task not found with id: 1", exception.getMessage());
        verify(taskRepository, times(1)).findById("1");
        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    void testGetTasksByStatus() {
        Task task1 = new Task();
        task1.setId("1");
        task1.setLabel("Task 1");
        task1.setCompleted(true);

        Task task2 = new Task();
        task2.setId("2"); 
        task2.setLabel("Task 2");
        task2.setCompleted(true);

        Page<Task> taskPage = mock(Page.class);
        Pageable pageable = mock(Pageable.class);
        
        when(taskRepository.findByCompleted(true, pageable)).thenReturn(taskPage);
        when(taskPage.getContent()).thenReturn(Arrays.asList(task1, task2));
        
        Page<Task> result = taskService.getTasksByStatus(true, pageable);
        
        assertEquals(2, result.getContent().size());
        verify(taskRepository, times(1)).findByCompleted(true, pageable);
        assertTrue(result.getContent().stream().allMatch(Task::isCompleted));
    }

}