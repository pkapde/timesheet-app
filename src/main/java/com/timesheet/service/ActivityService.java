package com.timesheet.service;

import com.timesheet.dto.ActivityDTO;
import com.timesheet.entity.Employee;
import com.timesheet.entity.Timesheet;
import com.timesheet.entity.TimesheetEntry;
import com.timesheet.entity.Project;
import com.timesheet.repository.EmployeeRepository;
import com.timesheet.repository.TimesheetRepository;
import com.timesheet.repository.TimesheetEntryRepository;
import com.timesheet.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private TimesheetEntryRepository timesheetEntryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<ActivityDTO> getRecentActivities(Long userId, int limit) {
        List<ActivityDTO> activities = new ArrayList<>();

        try {
            // Get recent timesheet activities
            List<Timesheet> recentTimesheets = timesheetRepository.findRecentTimesheetsByEmployee(userId, PageRequest.of(0, limit));

            for (Timesheet timesheet : recentTimesheets) {
                ActivityDTO activity = new ActivityDTO();

                if (timesheet.getApprovedAt() != null) {
                    activity.setIcon("fa-check-circle");
                    activity.setIconColor("#28a745");
                    activity.setTitle("Timesheet approved for week of " + formatDate(timesheet.getWeekStartDate()));
                    activity.setTime(getRelativeTime(timesheet.getApprovedAt()));
                    activity.setTimestamp(timesheet.getApprovedAt());
                } else if (timesheet.getSubmittedAt() != null) {
                    activity.setIcon("fa-paper-plane");
                    activity.setIconColor("#007bff");
                    activity.setTitle("Timesheet submitted for week of " + formatDate(timesheet.getWeekStartDate()));
                    activity.setTime(getRelativeTime(timesheet.getSubmittedAt()));
                    activity.setTimestamp(timesheet.getSubmittedAt());
                } else {
                    activity.setIcon("fa-edit");
                    activity.setIconColor("#ffc107");
                    activity.setTitle("Timesheet created for week of " + formatDate(timesheet.getWeekStartDate()));
                    activity.setTime(getRelativeTime(timesheet.getCreatedAt()));
                    activity.setTimestamp(timesheet.getCreatedAt());
                }

                activities.add(activity);
            }

            // Get recent timesheet entries
            List<TimesheetEntry> recentEntries = timesheetEntryRepository.findRecentEntriesByEmployee(userId, PageRequest.of(0, limit));

            for (TimesheetEntry entry : recentEntries) {
                ActivityDTO activity = new ActivityDTO();
                activity.setIcon("fa-clock");
                activity.setIconColor("#667eea");
                activity.setTitle("Logged " + entry.getHoursWorked() + " hours on " + entry.getProject().getProjectName());
                activity.setTime(getRelativeTime(entry.getTimesheet().getUpdatedAt()));
                activity.setTimestamp(entry.getTimesheet().getUpdatedAt());
                activities.add(activity);
            }

            // Sort all activities by timestamp (most recent first)
            activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

            // Return only the requested number of activities
            return activities.subList(0, Math.min(activities.size(), limit));

        } catch (Exception e) {
            System.err.println("Error fetching recent activities: " + e.getMessage());
            e.printStackTrace();
            return getDefaultActivities(); // Fallback to default activities
        }
    }

    public List<ActivityDTO> getManagerActivities(Long managerId, int limit) {
        List<ActivityDTO> activities = new ArrayList<>();

        try {
            // Get recent project activities for manager
            List<Project> managerProjects = projectRepository.findByProjectManagerId(managerId);

            for (Project project : managerProjects) {
                if (project.getCreatedAt() != null) {
                    ActivityDTO activity = new ActivityDTO();
                    activity.setIcon("fa-plus-circle");
                    activity.setIconColor("#667eea");
                    activity.setTitle("New project \"" + project.getProjectName() + "\" created");
                    activity.setTime(getRelativeTime(project.getCreatedAt()));
                    activity.setTimestamp(project.getCreatedAt());
                    activities.add(activity);
                }
            }

            // Get recent approval activities
            List<Timesheet> recentApprovals = timesheetRepository.findRecentApprovalsByManager(managerId, PageRequest.of(0, limit));

            for (Timesheet timesheet : recentApprovals) {
                if (timesheet.getApprovedAt() != null) {
                    ActivityDTO activity = new ActivityDTO();
                    activity.setIcon("fa-user-check");
                    activity.setIconColor("#28a745");
                    activity.setTitle("Approved timesheet for " + timesheet.getEmployee().getFullName());
                    activity.setTime(getRelativeTime(timesheet.getApprovedAt()));
                    activity.setTimestamp(timesheet.getApprovedAt());
                    activities.add(activity);
                }
            }

            // Sort activities by timestamp
            activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

            return activities.subList(0, Math.min(activities.size(), limit));

        } catch (Exception e) {
            System.err.println("Error fetching manager activities: " + e.getMessage());
            e.printStackTrace();
            return getDefaultActivities();
        }
    }

    private String formatDate(java.time.LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM dd"));
    }

    private String getRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 60) {
            return minutes <= 1 ? "1 minute ago" : minutes + " minutes ago";
        } else if (hours < 24) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (days < 30) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
    }

    private List<ActivityDTO> getDefaultActivities() {
        List<ActivityDTO> defaultActivities = new ArrayList<>();

        ActivityDTO activity1 = new ActivityDTO();
        activity1.setIcon("fa-info-circle");
        activity1.setIconColor("#17a2b8");
        activity1.setTitle("Welcome to Q Timesheet System");
        activity1.setTime("Just now");
        activity1.setTimestamp(LocalDateTime.now());

        defaultActivities.add(activity1);
        return defaultActivities;
    }
}
