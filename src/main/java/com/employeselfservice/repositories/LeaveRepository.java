package com.employeselfservice.repositories;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Leave;
import com.employeselfservice.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployee_IdAndStatus(Long employeeId, Leave.LeaveStatus status);

    List<Leave> findByEmployeeId(Long employeeId);

    @Query("SELECT COALESCE(SUM(l.days), 0) FROM Leave l WHERE l.employee.id = :employeeId AND MONTH(l.from) = :month AND YEAR(l.from) = YEAR(CURRENT_DATE)")
    double getTotalLeavesAppliedForEmployeeInMonth(@Param("employeeId") Long employeeId, @Param("month") int month);

    @Query("SELECT COALESCE(SUM(l.days), 0) FROM Leave l WHERE l.employee.id = :employeeId AND l.status = 'APPROVED' AND MONTH(l.from) = :month AND YEAR(l.from) = YEAR(CURRENT_DATE)")
    double getTotalLeavesApprovedForEmployeeInMonth(@Param("employeeId") Long employeeId, @Param("month") int month);

    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId AND MONTH(l.from) = :month AND YEAR(l.from) = :year")
    List<Leave> findAllByEmployeeAndMonth(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    Optional<Leave> findById(Long id);

    @Query("SELECT COALESCE(SUM(l.days), 0) FROM Leave l WHERE l.employee.id = :employeeId AND l.status = 'APPROVED' AND MONTH(l.from) = :month")
    double getOverflowForLatestApprovedLeaveInMonth(@Param("employeeId") Long employeeId, @Param("month") int month);

    @Query("SELECT l FROM Leave l WHERE l.employee.team = :team AND l.status = 'PENDING'")
    List<Leave> findAllPendingLeavesByTeam(@Param("team") Team team);

    @Query("SELECT l FROM Leave l WHERE l.employee.team = :team")
    List<Leave> findAllLeavesByTeam(@Param("team") Team team);

    @Query("SELECT l FROM Leave l WHERE l.employee = :employee AND MONTH(l.from) = :month AND YEAR(l.from) = :year")
    List<Leave> findAllLeavesForAnEmployee(@Param("employee") Employee employee, @Param("month") int month, @Param("year") int year);

}

