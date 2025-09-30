package com.timesheet.controller;

import com.timesheet.dto.ProjectDTO;
import com.timesheet.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectDTO dto) {
        try {
            ProjectDTO project = projectService.createProject(dto);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create project: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(@PathVariable Long id) {
        try {
            ProjectDTO project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Project not found: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        try {
            List<ProjectDTO> projects = projectService.getAllProjects();
            System.out.println("Found " + projects.size() + " projects");
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            System.err.println("Error fetching projects: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch projects: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveProjects() {
        try {
            List<ProjectDTO> projects = projectService.getActiveProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch active projects: " + e.getMessage());
        }
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<?> getProjectsByManager(@PathVariable Long managerId) {
        try {
            List<ProjectDTO> projects = projectService.getProjectsByManager(managerId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch projects by manager: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO dto) {
        try {
            ProjectDTO project = projectService.updateProject(id, dto);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update project: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProject(@PathVariable Long id) {
        projectService.deactivateProject(id);
        return ResponseEntity.ok().build();
    }
}
