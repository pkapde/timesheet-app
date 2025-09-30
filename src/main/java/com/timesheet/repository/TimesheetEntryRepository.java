package com.timesheet.repository;

import com.timesheet.entity.TimesheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {

    List<TimesheetEntry> findByTimesheetId(Long timesheetId);

    List<TimesheetEntry> findByProjectId(Long projectId);

    @Query("SELECT te FROM TimesheetEntry te WHERE te.timesheet.employee.id = :employeeId")
    List<TimesheetEntry> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT te FROM TimesheetEntry te WHERE te.workDate BETWEEN :startDate AND :endDate")
    List<TimesheetEntry> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT te FROM TimesheetEntry te WHERE te.project.id = :projectId AND te.workDate BETWEEN :startDate AND :endDate")
    List<TimesheetEntry> findByProjectIdAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT te FROM TimesheetEntry te WHERE te.timesheet.employee.id = :employeeId AND te.workDate BETWEEN :startDate AND :endDate")
    List<TimesheetEntry> findByEmployeeIdAndDateRange(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(te.hoursWorked) FROM TimesheetEntry te WHERE te.timesheet.employee.id = :employeeId AND te.workDate BETWEEN :startDate AND :endDate")
    Double getTotalHoursByEmployeeAndDateRange(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(te.hoursWorked) FROM TimesheetEntry te WHERE te.project.id = :projectId AND te.workDate BETWEEN :startDate AND :endDate")
    Double getTotalHoursByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT te FROM TimesheetEntry te WHERE te.timesheet.employee.id = :employeeId ORDER BY te.timesheet.updatedAt DESC")
    List<TimesheetEntry> findRecentEntriesByEmployee(@Param("employeeId") Long employeeId, Pageable pageable);
}
