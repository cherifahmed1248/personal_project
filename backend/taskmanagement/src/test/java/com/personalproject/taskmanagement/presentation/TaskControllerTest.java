package com.personalproject.taskmanagement.presentation;

import com.personalproject.taskmanagement.application.TaskService;
import com.personalproject.taskmanagement.domain.Task;
import com.personalproject.taskmanagement.domain.TaskNotFoundException;
import com.personalproject.taskmanagement.dto.TaskDTO;
import com.personalproject.taskmanagement.infrastructure.TaskRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.InjectMocks;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskController taskController;

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task();
        task.setId("1");
        task.setLabel("Task 1");
        task.setDescription("Description 1");
        task.setCompleted(false);

        when(taskService.getTaskById("1")).thenReturn(Optional.of(task));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Task 1"));

        verify(taskService, times(1)).getTaskById("1");
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        when(taskService.getTaskById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById("1");
    }

    @Test
    void testAddTask() throws Exception {
        Task task = new Task();
        task.setId("1");
        task.setLabel("Task 1");
        task.setDescription("Description 1");
        task.setCompleted(false);

        when(taskService.addTask(any(TaskDTO.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\",\"label\":\"Task 1\",\"description\":\"Description 1\",\"completed\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label").value("Task 1"));

        verify(taskService, times(1)).addTask(any(TaskDTO.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        Task task = new Task();
        task.setId("1");
        task.setLabel("Task 1");
        task.setDescription("Description 1");
        task.setCompleted(true);

        when(taskService.updateStatus("1")).thenReturn(task);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).updateStatus("1");
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        when(taskService.updateStatus("1")).thenThrow(new TaskNotFoundException("Task not found with id: 1"));

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateStatus("1");
    }

    
    @Test
    void testGetAllTasks() throws Exception {
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

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1, task2), pageable, 2);

        when(taskService.getAllTasks(any(Pageable.class))).thenReturn(taskPage);

        mockMvc.perform(get("/tasks")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].label").value("Task 1"))
                .andExpect(jsonPath("$.content[1].label").value("Task 2"));

        verify(taskService, times(1)).getAllTasks(any(Pageable.class));
    }

    @Test    
    void testGetAllTasksWithCompletedStatus() throws Exception {
        Task task1 = new Task();
        task1.setId("1");
        task1.setLabel("Task 1");
        task1.setDescription("Description 1");
        task1.setCompleted(true);

        Task task2 = new Task();
        task2.setId("2");
        task2.setLabel("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1, task2), pageable, 2);

        when(taskService.getTasksByStatus(true, pageable)).thenReturn(taskPage);

        mockMvc.perform(get("/tasks")
                .param("status", "completed")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(taskService, times(1)).getTasksByStatus(true, pageable);
    }
}