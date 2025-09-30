package com.timesheet.service;

import com.timesheet.dto.ProjectDTO;
import com.timesheet.entity.Employee;
import com.timesheet.entity.Project;
import com.timesheet.repository.EmployeeRepository;
import com.timesheet.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ProjectDTO createProject(ProjectDTO dto) {
        if (projectRepository.existsByProjectCode(dto.getProjectCode())) {
            throw new RuntimeException("Project with code " + dto.getProjectCode() + " already exists");
        }

        Project project = new Project();
        project.setProjectCode(dto.getProjectCode());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        if (dto.getProjectManagerId() != null) {
            Employee manager = employeeRepository.findById(dto.getProjectManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + dto.getProjectManagerId()));
            project.setProjectManager(manager);
        }

        Project saved = projectRepository.save(project);
        return mapToDTO(saved);
    }

    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return mapToDTO(project);
    }

    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProjectDTO> getActiveProjects() {
        List<Project> projects = projectRepository.findAllActiveProjects();
        return projects.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByManager(Long managerId) {
        List<Project> projects = projectRepository.findByProjectManagerId(managerId);
        return projects.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setIsActive(dto.getIsActive());

        if (dto.getProjectManagerId() != null) {
            Employee manager = employeeRepository.findById(dto.getProjectManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + dto.getProjectManagerId()));
            project.setProjectManager(manager);
        }

        Project saved = projectRepository.save(project);
        return mapToDTO(saved);
    }

    public void deactivateProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        project.setIsActive(false);
        projectRepository.save(project);
    }

    private ProjectDTO mapToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setProjectCode(project.getProjectCode());
        dto.setProjectName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setIsActive(project.getIsActive());

        if (project.getProjectManager() != null) {
            dto.setProjectManagerId(project.getProjectManager().getId());
            dto.setProjectManagerName(project.getProjectManager().getFullName());
        }

        return dto;
    }
}
