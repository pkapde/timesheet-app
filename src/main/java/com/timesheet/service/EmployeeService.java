package com.timesheet.service;

import com.timesheet.dto.EmployeeDTO;
import com.timesheet.entity.Employee;
import com.timesheet.enums.UserRole;
import com.timesheet.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public EmployeeDTO authenticateUser(String email, String password) {
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return mapToDTO(employee);
    }

    public EmployeeDTO createEmployee(EmployeeDTO dto, String password) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Employee with email " + dto.getEmail() + " already exists");
        }

        Employee employee = new Employee();
        employee.setEmail(dto.getEmail());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPassword(passwordEncoder.encode(password));
        employee.setRole(dto.getRole());

        if (dto.getManagerId() != null) {
            Employee manager = employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + dto.getManagerId()));
            employee.setManager(manager);
        }

        Employee saved = employeeRepository.save(employee);
        return mapToDTO(saved);
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToDTO(employee);
    }

    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Employee not found with email: " + email));
        return mapToDTO(employee);
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByRole(UserRole role) {
        List<Employee> employees = employeeRepository.findByRole(role);
        return employees.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<EmployeeDTO> getSubordinates(Long managerId) {
        List<Employee> subordinates = employeeRepository.findSubordinatesByManagerId(managerId);
        return subordinates.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setRole(dto.getRole());

        if (dto.getManagerId() != null && !dto.getManagerId().equals(employee.getManager() != null ? employee.getManager().getId() : null)) {
            Employee manager = employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + dto.getManagerId()));
            employee.setManager(manager);
        }

        Employee saved = employeeRepository.save(employee);
        return mapToDTO(saved);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private EmployeeDTO mapToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setEmail(employee.getEmail());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setRole(employee.getRole());
        dto.setFullName(employee.getFullName());

        if (employee.getManager() != null) {
            dto.setManagerId(employee.getManager().getId());
            dto.setManagerName(employee.getManager().getFullName());
        }

        return dto;
    }
}
