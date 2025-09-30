package com.timesheet.service;

import com.timesheet.dto.ReportDTO;
import com.timesheet.entity.TimesheetEntry;
import com.timesheet.repository.TimesheetEntryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private TimesheetEntryRepository timesheetEntryRepository;

    public ReportDTO getEmployeeReport(Long employeeId, LocalDate startDate, LocalDate endDate) {
        System.out.println("Generating employee report for ID: " + employeeId + " from " + startDate + " to " + endDate);

        try {
            List<TimesheetEntry> entries = timesheetEntryRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
            System.out.println("Found " + entries.size() + " timesheet entries for employee " + employeeId);

            Double totalHours = timesheetEntryRepository.getTotalHoursByEmployeeAndDateRange(employeeId, startDate, endDate);
            if (totalHours == null) totalHours = 0.0;

            Map<String, Double> projectHours = new HashMap<>();
            if (!entries.isEmpty()) {
                projectHours = entries.stream()
                    .filter(entry -> entry.getProject() != null)
                    .collect(Collectors.groupingBy(
                        entry -> entry.getProject().getProjectName(),
                        Collectors.summingDouble(TimesheetEntry::getHoursWorked)
                    ));
            }

            ReportDTO report = new ReportDTO();
            report.setEmployeeId(employeeId);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setTotalHours(totalHours);
            report.setProjectHours(projectHours);

            List<ReportDTO.ReportItem> reportItems = entries.stream()
                .map(this::mapEntryToReportItem)
                .collect(Collectors.toList());
            report.setEntries(reportItems);

            System.out.println("Employee report generated successfully with " + reportItems.size() + " entries and " + totalHours + " total hours");
            return report;
        } catch (Exception e) {
            System.err.println("Error generating employee report: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate employee report: " + e.getMessage());
        }
    }

    public ReportDTO getProjectReport(Long projectId, LocalDate startDate, LocalDate endDate) {
        System.out.println("Generating project report for ID: " + projectId + " from " + startDate + " to " + endDate);

        try {
            List<TimesheetEntry> entries = timesheetEntryRepository.findByProjectIdAndDateRange(projectId, startDate, endDate);
            System.out.println("Found " + entries.size() + " timesheet entries for project " + projectId);

            Double totalHours = timesheetEntryRepository.getTotalHoursByProjectAndDateRange(projectId, startDate, endDate);
            if (totalHours == null) totalHours = 0.0;

            Map<String, Double> employeeHours = new HashMap<>();
            if (!entries.isEmpty()) {
                employeeHours = entries.stream()
                    .filter(entry -> entry.getTimesheet() != null && entry.getTimesheet().getEmployee() != null)
                    .collect(Collectors.groupingBy(
                        entry -> entry.getTimesheet().getEmployee().getFullName(),
                        Collectors.summingDouble(TimesheetEntry::getHoursWorked)
                    ));
            }

            ReportDTO report = new ReportDTO();
            report.setProjectId(projectId);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setTotalHours(totalHours);
            report.setEmployeeHours(employeeHours);

            List<ReportDTO.ReportItem> reportItems = entries.stream()
                .map(this::mapEntryToReportItem)
                .collect(Collectors.toList());
            report.setEntries(reportItems);

            System.out.println("Project report generated successfully with " + reportItems.size() + " entries and " + totalHours + " total hours");
            return report;
        } catch (Exception e) {
            System.err.println("Error generating project report: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate project report: " + e.getMessage());
        }
    }

    public ReportDTO getManagerReport(Long managerId, LocalDate startDate, LocalDate endDate) {
        System.out.println("Generating manager report for ID: " + managerId + " from " + startDate + " to " + endDate);

        // Basic implementation - can be enhanced later
        ReportDTO report = new ReportDTO();
        report.setManagerId(managerId);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalHours(0.0);
        report.setEmployeeHours(new HashMap<>());
        report.setEntries(new ArrayList<>());

        return report;
    }

    public byte[] exportToExcel(ReportDTO report) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Timesheet Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Employee");
            headerRow.createCell(2).setCellValue("Project");
            headerRow.createCell(3).setCellValue("Hours");
            headerRow.createCell(4).setCellValue("Description");

            // Style header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < 5; i++) {
                headerRow.getCell(i).setCellStyle(headerStyle);
            }

            // Add data rows
            int rowNum = 1;
            if (report.getEntries() != null) {
                for (ReportDTO.ReportItem entry : report.getEntries()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(entry.getWorkDate().toString());
                    row.createCell(1).setCellValue(entry.getEmployeeName());
                    row.createCell(2).setCellValue(entry.getProjectName());
                    row.createCell(3).setCellValue(entry.getHoursWorked());
                    row.createCell(4).setCellValue(entry.getTaskDescription() != null ? entry.getTaskDescription() : "");
                }
            }

            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error creating Excel file", e);
        }
    }

    private ReportDTO.ReportItem mapEntryToReportItem(TimesheetEntry entry) {
        ReportDTO.ReportItem item = new ReportDTO.ReportItem();
        item.setWorkDate(entry.getWorkDate());
        item.setHoursWorked(entry.getHoursWorked());
        item.setTaskDescription(entry.getTaskDescription());

        // Safe null checks
        if (entry.getTimesheet() != null && entry.getTimesheet().getEmployee() != null) {
            item.setEmployeeName(entry.getTimesheet().getEmployee().getFullName());
        } else {
            item.setEmployeeName("Unknown Employee");
        }

        if (entry.getProject() != null) {
            item.setProjectName(entry.getProject().getProjectName());
            item.setProjectCode(entry.getProject().getProjectCode());
        } else {
            item.setProjectName("Unknown Project");
            item.setProjectCode("N/A");
        }

        return item;
    }
}
