package com.timesheet.dto;

import com.timesheet.enums.TimesheetStatus;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TimesheetDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;

    @NotNull(message = "Week start date is required")
    private LocalDate weekStartDate;

    @NotNull(message = "Week end date is required")
    private LocalDate weekEndDate;

    private TimesheetStatus status;
    private List<TimesheetEntryDTO> entries;
    private String rejectionComment;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private Double totalHours;

    // Constructors
    public TimesheetDTO() {}

    public TimesheetDTO(Long id, Long employeeId, String employeeName, LocalDate weekStartDate, LocalDate weekEndDate, TimesheetStatus status) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDate getWeekStartDate() { return weekStartDate; }
    public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }

    public LocalDate getWeekEndDate() { return weekEndDate; }
    public void setWeekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; }

    public TimesheetStatus getStatus() { return status; }
    public void setStatus(TimesheetStatus status) { this.status = status; }

    public List<TimesheetEntryDTO> getEntries() { return entries; }
    public void setEntries(List<TimesheetEntryDTO> entries) { this.entries = entries; }

    public String getRejectionComment() { return rejectionComment; }
    public void setRejectionComment(String rejectionComment) { this.rejectionComment = rejectionComment; }

    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }
}
