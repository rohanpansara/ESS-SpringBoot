package com.employeselfservice.repositories;

import com.employeselfservice.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND MONTH(a.date) = :month AND YEAR(a.date) = YEAR(CURRENT_DATE)")
    List<Attendance> findByEmployeeIdAndDateMonth(@Param("employeeId") Long employeeId, @Param("month") int month);

    @Query("SELECT a.workHours FROM Attendance a")
    List<String> findAllWorkHours();
}

