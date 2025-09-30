package com.timesheet.controller;

import com.timesheet.dto.ReportDTO;
import com.timesheet.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ReportDTO> getEmployeeReport(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            ReportDTO report = reportService.getEmployeeReport(employeeId, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error generating employee report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ReportDTO> getProjectReport(
            @PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            ReportDTO report = reportService.getProjectReport(projectId, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error generating project report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<ReportDTO> getManagerReport(
            @PathVariable Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            ReportDTO report = reportService.getManagerReport(managerId, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error generating manager report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/employee/{employeeId}/export")
    public ResponseEntity<byte[]> exportEmployeeReport(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            ReportDTO report = reportService.getEmployeeReport(employeeId, startDate, endDate);
            byte[] excelData = reportService.exportToExcel(report);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "employee_report_" + employeeId + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error exporting employee report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/project/{projectId}/export")
    public ResponseEntity<byte[]> exportProjectReport(
            @PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            ReportDTO report = reportService.getProjectReport(projectId, startDate, endDate);
            byte[] excelData = reportService.exportToExcel(report);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "project_report_" + projectId + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error exporting project report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
