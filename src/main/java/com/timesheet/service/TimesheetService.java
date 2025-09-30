package com.timesheet.service;

import com.timesheet.dto.*;
import com.timesheet.entity.*;
import com.timesheet.enums.*;
import com.timesheet.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimesheetService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private TimesheetEntryRepository entryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public TimesheetDTO createTimesheet(Long employeeId, TimesheetCreateDTO dto) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        // Check if timesheet already exists for this week
        if (timesheetRepository.findByEmployeeIdAndWeekStartDate(employeeId, dto.getWeekStartDate()).isPresent()) {
            throw new RuntimeException("Timesheet already exists for this week");
        }

        Timesheet timesheet = new Timesheet();
        timesheet.setEmployee(employee);
        timesheet.setWeekStartDate(dto.getWeekStartDate());
        timesheet.setWeekEndDate(dto.getWeekEndDate());
        timesheet.setStatus(TimesheetStatus.DRAFT);

        Timesheet saved = timesheetRepository.save(timesheet);
        return mapToDTO(saved);
    }

    public TimesheetEntryDTO addEntry(Long timesheetId, EntryCreateDTO dto) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
            .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));

        if (timesheet.getStatus() != TimesheetStatus.DRAFT) {
            throw new RuntimeException("Cannot modify timesheet that is not in draft status");
        }

        Project project = projectRepository.findById(dto.getProjectId())
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.getProjectId()));

        TimesheetEntry entry = new TimesheetEntry();
        entry.setTimesheet(timesheet);
        entry.setProject(project);
        entry.setWorkDate(dto.getWorkDate());
        entry.setHoursWorked(dto.getHoursWorked());
        entry.setTaskDescription(dto.getTaskDescription());

        TimesheetEntry saved = entryRepository.save(entry);
        return mapToEntryDTO(saved);
    }

    public void submitTimesheet(Long timesheetId, Long employeeId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
            .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));

        if (!timesheet.getEmployee().getId().equals(employeeId)) {
            throw new RuntimeException("You can only submit your own timesheets");
        }

        if (timesheet.getStatus() != TimesheetStatus.DRAFT) {
            throw new RuntimeException("Only draft timesheets can be submitted");
        }

        timesheet.setStatus(TimesheetStatus.SUBMITTED);
        timesheet.setSubmittedAt(LocalDateTime.now());
        timesheetRepository.save(timesheet);
    }

    public void approveTimesheet(Long timesheetId, Long managerId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
            .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));

        Employee manager = employeeRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));

        if (timesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted timesheets can be approved");
        }

        timesheet.setStatus(TimesheetStatus.APPROVED);
        timesheet.setApprovedBy(manager);
        timesheet.setApprovedAt(LocalDateTime.now());
        timesheetRepository.save(timesheet);
    }

    public void rejectTimesheet(Long timesheetId, Long managerId, String comment) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
            .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));

        if (timesheet.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted timesheets can be rejected");
        }

        timesheet.setStatus(TimesheetStatus.REJECTED);
        timesheet.setRejectionComment(comment);
        timesheetRepository.save(timesheet);
    }

    public List<TimesheetDTO> getEmployeeTimesheets(Long employeeId) {
        List<Timesheet> timesheets = timesheetRepository.findByEmployeeId(employeeId);
        return timesheets.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<TimesheetDTO> getPendingApprovalsForManager(Long managerId) {
        List<Timesheet> timesheets = timesheetRepository.findPendingTimesheetsByManagerId(managerId, TimesheetStatus.SUBMITTED);
        return timesheets.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public TimesheetDTO getTimesheetById(Long timesheetId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
            .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timesheetId));
        return mapToDTO(timesheet);
    }

    private TimesheetDTO mapToDTO(Timesheet timesheet) {
        TimesheetDTO dto = new TimesheetDTO();
        dto.setId(timesheet.getId());
        dto.setEmployeeId(timesheet.getEmployee().getId());
        dto.setEmployeeName(timesheet.getEmployee().getFullName());
        dto.setWeekStartDate(timesheet.getWeekStartDate());
        dto.setWeekEndDate(timesheet.getWeekEndDate());
        dto.setStatus(timesheet.getStatus());
        dto.setRejectionComment(timesheet.getRejectionComment());
        dto.setSubmittedAt(timesheet.getSubmittedAt());
        dto.setApprovedAt(timesheet.getApprovedAt());

        if (timesheet.getApprovedBy() != null) {
            dto.setApprovedById(timesheet.getApprovedBy().getId());
            dto.setApprovedByName(timesheet.getApprovedBy().getFullName());
        }

        if (timesheet.getEntries() != null) {
            List<TimesheetEntryDTO> entryDTOs = timesheet.getEntries().stream()
                .map(this::mapToEntryDTO)
                .collect(Collectors.toList());
            dto.setEntries(entryDTOs);
            dto.setTotalHours(timesheet.getTotalHours());
        }

        return dto;
    }

    private TimesheetEntryDTO mapToEntryDTO(TimesheetEntry entry) {
        TimesheetEntryDTO dto = new TimesheetEntryDTO();
        dto.setId(entry.getId());
        dto.setTimesheetId(entry.getTimesheet().getId());
        dto.setProjectId(entry.getProject().getId());
        dto.setProjectCode(entry.getProject().getProjectCode());
        dto.setProjectName(entry.getProject().getProjectName());
        dto.setWorkDate(entry.getWorkDate());
        dto.setHoursWorked(entry.getHoursWorked());
        dto.setTaskDescription(entry.getTaskDescription());
        return dto;
    }
}
