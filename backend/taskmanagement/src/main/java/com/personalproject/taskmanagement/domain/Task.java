package com.personalproject.taskmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Task {
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("label")
    @NotBlank(message = "Label is mandatory")
    private String label;

    @JsonProperty("description")
    @Size(max = 4096, message = "Description cannot exceed 4096 characters")
    private String description;

    @JsonProperty("completed")
    private boolean completed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}