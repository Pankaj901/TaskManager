package com.pkp.TaskManager.model;

import com.pkp.TaskManager.dto.TaskDto;
import jakarta.persistence.*;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.CREATED;

    public Task() {}

    public Task(String title) {
        this.title = title;
        this.status = TaskStatus.CREATED;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskDto toDto() {
        return new TaskDto(
                String.valueOf(id),
                title,
                description,
                status.name()
        );
    }
}
