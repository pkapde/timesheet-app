package com.timesheet.entity;

import com.timesheet.enums.TimesheetStatus;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "timesheets")
public class Timesheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate weekStartDate;

    @Column(nullable = false)
    private LocalDate weekEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimesheetStatus status = TimesheetStatus.DRAFT;

    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TimesheetEntry> entries;

    @Column(name = "total_hours")
    private Double totalHours = 0.0;

    private String rejectionComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;

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
    public Timesheet() {}

    public Timesheet(Employee employee, LocalDate weekStartDate, LocalDate weekEndDate) {
        this.employee = employee;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getWeekStartDate() { return weekStartDate; }
    public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }

    public LocalDate getWeekEndDate() { return weekEndDate; }
    public void setWeekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; }

    public TimesheetStatus getStatus() { return status; }
    public void setStatus(TimesheetStatus status) { this.status = status; }

    public Set<TimesheetEntry> getEntries() { return entries; }
    public void setEntries(Set<TimesheetEntry> entries) { this.entries = entries; }

    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }

    public String getRejectionComment() { return rejectionComment; }
    public void setRejectionComment(String rejectionComment) { this.rejectionComment = rejectionComment; }

    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
