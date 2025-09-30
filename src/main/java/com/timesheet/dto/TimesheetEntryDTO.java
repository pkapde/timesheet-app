package com.timesheet.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class TimesheetEntryDTO {
    private Long id;
    private Long timesheetId;

    @NotNull(message = "Project is required")
    private Long projectId;

    private String projectCode;
    private String projectName;

    @NotNull(message = "Work date is required")
    private LocalDate workDate;

    @NotNull(message = "Hours worked is required")
    @DecimalMin(value = "0.0", message = "Hours worked must be positive")
    @DecimalMax(value = "24.0", message = "Hours worked cannot exceed 24 hours per day")
    private Double hoursWorked;

    private String taskDescription;

    // Constructors
    public TimesheetEntryDTO() {}

    public TimesheetEntryDTO(Long id, Long projectId, String projectCode, String projectName, LocalDate workDate, Double hoursWorked, String taskDescription) {
        this.id = id;
        this.projectId = projectId;
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.taskDescription = taskDescription;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTimesheetId() { return timesheetId; }
    public void setTimesheetId(Long timesheetId) { this.timesheetId = timesheetId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
}
