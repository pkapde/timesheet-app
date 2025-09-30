package com.timesheet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/app")
    public String app() {
        return "index.html";
    }
}

@RestController
class ApiWelcomeController {

    @GetMapping("/api")
    public String welcome() {
        return "<!DOCTYPE html>" +
               "<html><head><title>Timesheet API</title></head>" +
               "<body>" +
               "<h1>🕒 Timesheet Management System API</h1>" +
               "<p>Welcome to the Timesheet Application API!</p>" +
               "<h3>Available Endpoints:</h3>" +
               "<ul>" +
               "<li><strong>Web App:</strong> <a href='/'>Launch Application</a></li>" +
               "<li><strong>Database Console:</strong> <a href='/h2-console'>H2 Console</a></li>" +
               "<li><strong>Authentication:</strong> POST /api/auth/login</li>" +
               "<li><strong>Timesheets:</strong> /api/timesheets/*</li>" +
               "<li><strong>Employees:</strong> /api/employees/*</li>" +
               "<li><strong>Projects:</strong> /api/projects/*</li>" +
               "<li><strong>Reports:</strong> /api/reports/*</li>" +
               "</ul>" +
               "<h3>Test Accounts:</h3>" +
               "<ul>" +
               "<li><strong>Admin:</strong> admin@timesheet.com / admin123</li>" +
               "<li><strong>Manager:</strong> manager@timesheet.com / manager123</li>" +
               "<li><strong>Employee:</strong> employee1@timesheet.com / emp123</li>" +
               "</ul>" +
               "<p><em>Application running on port 9091</em></p>" +
               "</body></html>";
    }

    @GetMapping("/api/health")
    public String health() {
        return "{\"status\":\"UP\",\"message\":\"Timesheet application is running successfully!\"}";
    }
}
