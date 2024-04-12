package com.employeselfservice.repositories;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByEmail(String email);

    Employee findById(long id);

    @Query("SELECT e FROM Employee e WHERE e.team = :team AND e.designation.id != 1")
    List<Employee> findEmployeesInTeamExcludingDesignation(Team team);
}


