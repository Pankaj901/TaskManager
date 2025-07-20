package com.pkp.TaskManager.controller;

import com.pkp.TaskManager.dto.TaskDto;
import com.pkp.TaskManager.model.Task;
import com.pkp.TaskManager.model.TaskStatus;
import com.pkp.TaskManager.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;



@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    // 1. Create task
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto) {
        if (taskDto.getTitle() == null || taskDto.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Task task = new Task(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        Task saved = taskRepository.save(task);
        return new ResponseEntity<>(saved.toDto(), HttpStatus.CREATED);
    }

    // 2. Get one task
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok(task.toDto()))
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Update task
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Task existingTask = taskOptional.get();

        // Validate status if provided
        if (taskDto.getStatus() != null) {
            try {
                TaskStatus.valueOf(taskDto.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                String msg = "Available statuses are: CREATED, APPROVED, REJECTED, BLOCKED, DONE.";
                return new ResponseEntity<>(msg, HttpStatus.OK);
            }
        }

        if (taskDto.getTitle() != null) {
            existingTask.setTitle(taskDto.getTitle());
        }

        if (taskDto.getDescription() != null) {
            existingTask.setDescription(taskDto.getDescription());
        }

        if (taskDto.getStatus() != null) {
            existingTask.setStatus(TaskStatus.valueOf(taskDto.getStatus().toUpperCase()));
        }

        taskRepository.save(existingTask);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 4. Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 5. Get all tasks
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> taskList = new ArrayList<>();
        taskRepository.findAll().forEach(task -> taskList.add(task.toDto()));

        if (taskList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(taskList);
    }

    // 6. Describe one
    @GetMapping("/describe/{id}")
    public ResponseEntity<String> describeTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    String desc = String.format("Description of Task [%d : %s] is: %s",
                            task.getId(), task.getTitle(), task.getDescription());
                    return ResponseEntity.ok(desc);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 7. Describe all
    @GetMapping("/describe")
    public List<String> describeAllTasks() {
        List<String> descriptions = new ArrayList<>();
        taskRepository.findAll().forEach(task -> {
            descriptions.add(String.format("Description of Task [%d : %s] is: %s",
                    task.getId(), task.getTitle(), task.getDescription()));
        });
        return descriptions;
    }
}
