package com.timesheet.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "timesheet_entries")
public class TimesheetEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheet_id", nullable = false)
    private Timesheet timesheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    @NotNull
    private LocalDate workDate;

    @Column(nullable = false)
    @NotNull
    @DecimalMin(value = "0.0", message = "Hours worked must be positive")
    @DecimalMax(value = "24.0", message = "Hours worked cannot exceed 24 hours per day")
    private Double hoursWorked;

    @Column(length = 500)
    private String taskDescription;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public TimesheetEntry() {}

    public TimesheetEntry(Timesheet timesheet, Project project, LocalDate workDate, Double hoursWorked, String taskDescription) {
        this.timesheet = timesheet;
        this.project = project;
        this.workDate = workDate;
        this.hoursWorked = hoursWorked;
        this.taskDescription = taskDescription;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Timesheet getTimesheet() { return timesheet; }
    public void setTimesheet(Timesheet timesheet) { this.timesheet = timesheet; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
