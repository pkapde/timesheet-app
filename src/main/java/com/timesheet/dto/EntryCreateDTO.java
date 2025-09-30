package com.timesheet.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class EntryCreateDTO {
    @NotNull(message = "Project is required")
    private Long projectId;

    @NotNull(message = "Work date is required")
    private LocalDate workDate;

    @NotNull(message = "Hours worked is required")
    @DecimalMin(value = "0.0", message = "Hours worked must be positive")
    @DecimalMax(value = "24.0", message = "Hours worked cannot exceed 24 hours per day")
    private Double hoursWorked;

    private String taskDescription;

    // Constructors
    public EntryCreateDTO() {}

    public EntryCreateDTO(Long projectId, LocalDate workDate, Double hoursWorked, String taskDescription) {
        this.projectId = projectId;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.taskDescription = taskDescription;
    }

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
}
