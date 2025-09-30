package com.timesheet.service;

import com.timesheet.entity.Employee;
import com.timesheet.entity.Project;
import com.timesheet.entity.Timesheet;
import com.timesheet.entity.TimesheetEntry;
import com.timesheet.enums.UserRole;
import com.timesheet.enums.TimesheetStatus;
import com.timesheet.repository.EmployeeRepository;
import com.timesheet.repository.ProjectRepository;
import com.timesheet.repository.TimesheetEntryRepository;
import com.timesheet.repository.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Add these repositories for creating sample timesheet data
    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private TimesheetEntryRepository timesheetEntryRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Create Admin user
        if (!employeeRepository.existsByEmail("admin@timesheet.com")) {
            Employee admin = new Employee();
            admin.setEmail("admin@timesheet.com");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            employeeRepository.save(admin);
        }

        // Create Manager
        if (!employeeRepository.existsByEmail("manager@timesheet.com")) {
            Employee manager = new Employee();
            manager.setEmail("manager@timesheet.com");
            manager.setFirstName("John");
            manager.setLastName("Manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRole(UserRole.MANAGER);
            employeeRepository.save(manager);
        }

        // Create Employees
        Employee manager = employeeRepository.findByEmail("manager@timesheet.com").orElse(null);

        if (!employeeRepository.existsByEmail("employee1@timesheet.com")) {
            Employee employee1 = new Employee();
            employee1.setEmail("employee1@timesheet.com");
            employee1.setFirstName("Alice");
            employee1.setLastName("Smith");
            employee1.setPassword(passwordEncoder.encode("emp123"));
            employee1.setRole(UserRole.EMPLOYEE);
            employee1.setManager(manager);
            employeeRepository.save(employee1);
        }

        if (!employeeRepository.existsByEmail("employee2@timesheet.com")) {
            Employee employee2 = new Employee();
            employee2.setEmail("employee2@timesheet.com");
            employee2.setFirstName("Bob");
            employee2.setLastName("Johnson");
            employee2.setPassword(passwordEncoder.encode("emp123"));
            employee2.setRole(UserRole.EMPLOYEE);
            employee2.setManager(manager);
            employeeRepository.save(employee2);
        }

        // Create Sample Projects
        if (!projectRepository.existsByProjectCode("PROJ001")) {
            Project project1 = new Project();
            project1.setProjectCode("PROJ001");
            project1.setProjectName("E-commerce Platform");
            project1.setDescription("Development of new e-commerce platform");
            project1.setStartDate(LocalDate.now().minusMonths(6));
            project1.setEndDate(LocalDate.now().plusMonths(6));
            project1.setProjectManager(manager);
            project1.setIsActive(true);
            projectRepository.save(project1);
        }

        if (!projectRepository.existsByProjectCode("PROJ002")) {
            Project project2 = new Project();
            project2.setProjectCode("PROJ002");
            project2.setProjectName("Mobile App Development");
            project2.setDescription("iOS and Android mobile application");
            project2.setStartDate(LocalDate.now().minusMonths(3));
            project2.setEndDate(LocalDate.now().plusMonths(9));
            project2.setProjectManager(manager);
            project2.setIsActive(true);
            projectRepository.save(project2);
        }

        if (!projectRepository.existsByProjectCode("PROJ003")) {
            Project project3 = new Project();
            project3.setProjectCode("PROJ003");
            project3.setProjectName("Database Migration");
            project3.setDescription("Legacy system database migration");
            project3.setStartDate(LocalDate.now().minusMonths(1));
            project3.setEndDate(LocalDate.now().plusMonths(3));
            project3.setProjectManager(manager);
            project3.setIsActive(true);
            projectRepository.save(project3);
        }

        // Create sample timesheet entries for testing reports
        createSampleTimesheetData();

        System.out.println("Sample data initialized successfully!");
        System.out.println("Login credentials:");
        System.out.println("Admin: admin@timesheet.com / admin123");
        System.out.println("Manager: manager@timesheet.com / manager123");
        System.out.println("Employee1: employee1@timesheet.com / emp123");
        System.out.println("Employee2: employee2@timesheet.com / emp123");
    }

    private void createSampleTimesheetData() {
        try {
            Employee employee1 = employeeRepository.findByEmail("employee1@timesheet.com").orElse(null);
            Employee employee2 = employeeRepository.findByEmail("employee2@timesheet.com").orElse(null);

            Project project1 = projectRepository.findByProjectCode("PROJ001").orElse(null);
            Project project2 = projectRepository.findByProjectCode("PROJ002").orElse(null);

            if (employee1 != null && project1 != null && project2 != null) {
                // Check if timesheet already exists to avoid duplicates
                if (timesheetRepository.findByEmployeeAndWeekStartDate(employee1, LocalDate.now().minusWeeks(1)).isEmpty()) {
                    // Create timesheet for employee1
                    Timesheet timesheet1 = new Timesheet();
                    timesheet1.setEmployee(employee1);
                    timesheet1.setWeekStartDate(LocalDate.now().minusWeeks(1));
                    timesheet1.setWeekEndDate(LocalDate.now().minusWeeks(1).plusDays(6));
                    timesheet1.setStatus(TimesheetStatus.SUBMITTED);
                    timesheet1.setSubmittedAt(LocalDateTime.now().minusDays(2));
                    timesheet1 = timesheetRepository.save(timesheet1);

                    // Create timesheet entries
                    TimesheetEntry entry1 = new TimesheetEntry();
                    entry1.setTimesheet(timesheet1);
                    entry1.setProject(project1);
                    entry1.setWorkDate(LocalDate.now().minusWeeks(1));
                    entry1.setHoursWorked(8.0);
                    entry1.setTaskDescription("Frontend development work");
                    timesheetEntryRepository.save(entry1);

                    TimesheetEntry entry2 = new TimesheetEntry();
                    entry2.setTimesheet(timesheet1);
                    entry2.setProject(project2);
                    entry2.setWorkDate(LocalDate.now().minusWeeks(1).plusDays(1));
                    entry2.setHoursWorked(6.5);
                    entry2.setTaskDescription("API integration testing");
                    timesheetEntryRepository.save(entry2);

                    TimesheetEntry entry3 = new TimesheetEntry();
                    entry3.setTimesheet(timesheet1);
                    entry3.setProject(project1);
                    entry3.setWorkDate(LocalDate.now().minusWeeks(1).plusDays(2));
                    entry3.setHoursWorked(7.0);
                    entry3.setTaskDescription("Database schema updates");
                    timesheetEntryRepository.save(entry3);

                    // Update timesheet total hours
                    timesheet1.setTotalHours(21.5);
                    timesheetRepository.save(timesheet1);
                }
            }

            if (employee2 != null && project1 != null && project2 != null) {
                // Check if timesheet already exists to avoid duplicates
                if (timesheetRepository.findByEmployeeAndWeekStartDate(employee2, LocalDate.now().minusWeeks(1)).isEmpty()) {
                    // Create timesheet for employee2
                    Timesheet timesheet2 = new Timesheet();
                    timesheet2.setEmployee(employee2);
                    timesheet2.setWeekStartDate(LocalDate.now().minusWeeks(1));
                    timesheet2.setWeekEndDate(LocalDate.now().minusWeeks(1).plusDays(6));
                    timesheet2.setStatus(TimesheetStatus.APPROVED);
                    timesheet2.setSubmittedAt(LocalDateTime.now().minusDays(3));
                    timesheet2.setApprovedAt(LocalDateTime.now().minusDays(1));
                    timesheet2 = timesheetRepository.save(timesheet2);

                    // Create timesheet entries
                    TimesheetEntry entry4 = new TimesheetEntry();
                    entry4.setTimesheet(timesheet2);
                    entry4.setProject(project2);
                    entry4.setWorkDate(LocalDate.now().minusWeeks(1));
                    entry4.setHoursWorked(8.0);
                    entry4.setTaskDescription("Mobile UI development");
                    timesheetEntryRepository.save(entry4);

                    TimesheetEntry entry5 = new TimesheetEntry();
                    entry5.setTimesheet(timesheet2);
                    entry5.setProject(project1);
                    entry5.setWorkDate(LocalDate.now().minusWeeks(1).plusDays(1));
                    entry5.setHoursWorked(4.0);
                    entry5.setTaskDescription("Code review and bug fixes");
                    timesheetEntryRepository.save(entry5);

                    // Update timesheet total hours
                    timesheet2.setTotalHours(12.0);
                    timesheetRepository.save(timesheet2);
                }
            }

            System.out.println("Sample timesheet data created successfully!");

        } catch (Exception e) {
            System.err.println("Error creating sample timesheet data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
