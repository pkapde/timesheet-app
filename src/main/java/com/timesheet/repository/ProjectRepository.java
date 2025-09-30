package com.timesheet.repository;

import com.timesheet.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectCode(String projectCode);

    boolean existsByProjectCode(String projectCode);

    List<Project> findByIsActive(Boolean isActive);

    @Query("SELECT p FROM Project p WHERE p.projectManager.id = :managerId")
    List<Project> findByProjectManagerId(@Param("managerId") Long managerId);

    @Query("SELECT p FROM Project p WHERE p.isActive = true ORDER BY p.projectName")
    List<Project> findAllActiveProjects();
}
