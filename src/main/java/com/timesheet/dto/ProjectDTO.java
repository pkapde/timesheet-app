package com.timesheet.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Project code is required")
    private String projectCode;

    @NotBlank(message = "Project name is required")
    private String projectName;

    private String description;
    private Long projectManagerId;
    private String projectManagerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;

    // Constructors
    public ProjectDTO() {}

    public ProjectDTO(Long id, String projectCode, String projectName, String description) {
        this.id = id;
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getProjectManagerId() { return projectManagerId; }
    public void setProjectManagerId(Long projectManagerId) { this.projectManagerId = projectManagerId; }

    public String getProjectManagerName() { return projectManagerName; }
    public void setProjectManagerName(String projectManagerName) { this.projectManagerName = projectManagerName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
