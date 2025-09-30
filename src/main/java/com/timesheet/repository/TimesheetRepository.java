package com.timesheet.repository;

import com.timesheet.entity.Employee;
import com.timesheet.entity.Timesheet;
import com.timesheet.enums.TimesheetStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {

    List<Timesheet> findByEmployeeId(Long employeeId);

    List<Timesheet> findByStatus(TimesheetStatus status);

    @Query("SELECT t FROM Timesheet t WHERE t.employee.id = :employeeId AND t.status = :status")
    List<Timesheet> findByEmployeeIdAndStatus(@Param("employeeId") Long employeeId, @Param("status") TimesheetStatus status);

    @Query("SELECT t FROM Timesheet t WHERE t.employee.id = :employeeId AND t.weekStartDate = :weekStartDate")
    Optional<Timesheet> findByEmployeeIdAndWeekStartDate(@Param("employeeId") Long employeeId, @Param("weekStartDate") LocalDate weekStartDate);

    @Query("SELECT t FROM Timesheet t WHERE t.employee = :employee AND t.weekStartDate = :weekStartDate")
    List<Timesheet> findByEmployeeAndWeekStartDate(@Param("employee") Employee employee, @Param("weekStartDate") LocalDate weekStartDate);

    @Query("SELECT t FROM Timesheet t WHERE t.employee.manager.id = :managerId AND t.status = :status")
    List<Timesheet> findPendingTimesheetsByManagerId(@Param("managerId") Long managerId, @Param("status") TimesheetStatus status);

    @Query("SELECT t FROM Timesheet t WHERE t.weekStartDate BETWEEN :startDate AND :endDate")
    List<Timesheet> findTimesheetsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Timesheet t WHERE t.employee.id = :employeeId AND t.weekStartDate BETWEEN :startDate AND :endDate")
    List<Timesheet> findByEmployeeIdAndDateRange(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Timesheet t WHERE t.employee.id = :employeeId ORDER BY t.updatedAt DESC")
    List<Timesheet> findRecentTimesheetsByEmployee(@Param("employeeId") Long employeeId, Pageable pageable);

    @Query("SELECT t FROM Timesheet t WHERE t.approvedBy.id = :managerId AND t.approvedAt IS NOT NULL ORDER BY t.approvedAt DESC")
    List<Timesheet> findRecentApprovalsByManager(@Param("managerId") Long managerId, Pageable pageable);
}
