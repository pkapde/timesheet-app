package com.timesheet.repository;

import com.timesheet.entity.Employee;
import com.timesheet.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByRole(UserRole role);

    @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId")
    List<Employee> findSubordinatesByManagerId(@Param("managerId") Long managerId);

    @Query("SELECT e FROM Employee e WHERE e.role = :role AND e.manager.id = :managerId")
    List<Employee> findByRoleAndManagerId(@Param("role") UserRole role, @Param("managerId") Long managerId);
}
