package com.timesheet.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportDTO {
    private Long employeeId;
    private Long projectId;
    private Long managerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalHours;
    private Map<String, Double> projectHours;
    private Map<String, Double> employeeHours;
    private List<ReportItem> entries;

    // Constructors
    public ReportDTO() {}

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }

    public Map<String, Double> getProjectHours() { return projectHours; }
    public void setProjectHours(Map<String, Double> projectHours) { this.projectHours = projectHours; }

    public Map<String, Double> getEmployeeHours() { return employeeHours; }
    public void setEmployeeHours(Map<String, Double> employeeHours) { this.employeeHours = employeeHours; }

    public List<ReportItem> getEntries() { return entries; }
    public void setEntries(List<ReportItem> entries) { this.entries = entries; }

    // Inner class for report items
    public static class ReportItem {
        private LocalDate workDate;
        private String employeeName;
        private String projectName;
        private String projectCode;
        private Double hoursWorked;
        private String taskDescription;

        // Constructors
        public ReportItem() {}

        // Getters and Setters
        public LocalDate getWorkDate() { return workDate; }
        public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public String getProjectCode() { return projectCode; }
        public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

        public Double getHoursWorked() { return hoursWorked; }
        public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

        public String getTaskDescription() { return taskDescription; }
        public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    }
}
