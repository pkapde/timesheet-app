package com.timesheet.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class TimesheetCreateDTO {
    @NotNull(message = "Week start date is required")
    private LocalDate weekStartDate;

    @NotNull(message = "Week end date is required")
    private LocalDate weekEndDate;

    // Constructors
    public TimesheetCreateDTO() {}

    public TimesheetCreateDTO(LocalDate weekStartDate, LocalDate weekEndDate) {
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
    }

    // Getters and Setters
    public LocalDate getWeekStartDate() { return weekStartDate; }
    public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }

    public LocalDate getWeekEndDate() { return weekEndDate; }
    public void setWeekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; }
}
