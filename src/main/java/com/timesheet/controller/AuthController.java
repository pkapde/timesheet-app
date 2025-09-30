package com.timesheet.controller;

import com.timesheet.dto.EmployeeDTO;
import com.timesheet.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            EmployeeDTO employee = employeeService.authenticateUser(email, password);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody EmployeeDTO dto, @RequestParam String password) {
        try {
            EmployeeDTO employee = employeeService.createEmployee(dto, password);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getProfile() {
        Map<String, String> profile = new HashMap<>();
        profile.put("status", "OK");
        profile.put("message", "User profile endpoint");
        return ResponseEntity.ok(profile);
    }
}
