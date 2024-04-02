package com.employeselfservice.repositories;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    // Method to get all projects an employee is working on
    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.employee.id = :employeeId")
    List<Project> findAllProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM Project p WHERE p.owner.id = :employeeId")
    List<Project> findAllProjectsOwnedByEmployee(@Param("employeeId") Long employeeId);

}
