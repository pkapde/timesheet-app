package com.timesheet.controller;

import com.timesheet.dto.EmployeeDTO;
import com.timesheet.enums.UserRole;
import com.timesheet.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<?> createEmployee(
            @Valid @RequestBody EmployeeDTO dto,
            @RequestParam String password) {
        try {
            System.out.println("Creating employee with data: " + dto.getEmail() + ", " + dto.getFirstName() + " " + dto.getLastName());
            EmployeeDTO employee = employeeService.createEmployee(dto, password);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            System.err.println("Error creating employee: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create employee: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Employee not found: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        try {
            List<EmployeeDTO> employees = employeeService.getAllEmployees();
            System.out.println("Found " + employees.size() + " employees");
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            System.err.println("Error fetching employees: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch employees: " + e.getMessage());
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getEmployeesByRole(@PathVariable UserRole role) {
        try {
            List<EmployeeDTO> employees = employeeService.getEmployeesByRole(role);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch employees by role: " + e.getMessage());
        }
    }

    @GetMapping("/subordinates/{managerId}")
    public ResponseEntity<?> getSubordinates(@PathVariable Long managerId) {
        try {
            List<EmployeeDTO> subordinates = employeeService.getSubordinates(managerId);
            return ResponseEntity.ok(subordinates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch subordinates: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO dto) {
        try {
            EmployeeDTO employee = employeeService.updateEmployee(id, dto);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update employee: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete employee: " + e.getMessage());
        }
    }
}
