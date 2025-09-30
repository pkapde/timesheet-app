package com.timesheet.controller;

import com.timesheet.dto.*;
import com.timesheet.service.TimesheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/timesheets")
@CrossOrigin(origins = "*")
public class TimesheetController {

    @Autowired
    private TimesheetService timesheetService;

    @PostMapping
    public ResponseEntity<TimesheetDTO> createTimesheet(
            @RequestParam Long employeeId,
            @Valid @RequestBody TimesheetCreateDTO dto) {
        TimesheetDTO timesheet = timesheetService.createTimesheet(employeeId, dto);
        return ResponseEntity.ok(timesheet);
    }

    @PostMapping("/{id}/entries")
    public ResponseEntity<TimesheetEntryDTO> addEntry(
            @PathVariable Long id,
            @Valid @RequestBody EntryCreateDTO dto) {
        TimesheetEntryDTO entry = timesheetService.addEntry(id, dto);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<Void> submitTimesheet(
            @PathVariable Long id,
            @RequestParam Long employeeId) {
        timesheetService.submitTimesheet(id, employeeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveTimesheet(
            @PathVariable Long id,
            @RequestParam Long managerId) {
        timesheetService.approveTimesheet(id, managerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectTimesheet(
            @PathVariable Long id,
            @RequestParam Long managerId,
            @RequestParam String comment) {
        timesheetService.rejectTimesheet(id, managerId, comment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TimesheetDTO>> getEmployeeTimesheets(@PathVariable Long employeeId) {
        List<TimesheetDTO> timesheets = timesheetService.getEmployeeTimesheets(employeeId);
        return ResponseEntity.ok(timesheets);
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<TimesheetDTO>> getPendingApprovals(@RequestParam Long managerId) {
        List<TimesheetDTO> timesheets = timesheetService.getPendingApprovalsForManager(managerId);
        return ResponseEntity.ok(timesheets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimesheetDTO> getTimesheet(@PathVariable Long id) {
        TimesheetDTO timesheet = timesheetService.getTimesheetById(id);
        return ResponseEntity.ok(timesheet);
    }
}
