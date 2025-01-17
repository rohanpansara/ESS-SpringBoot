package com.employeselfservice.repositories;

import com.employeselfservice.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    // Method to get all projects an employee is working on
    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.employee.id = :employeeId")
    List<Project> findAllProjectsAssignedToTheEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM Project p WHERE p.owner.id = :employeeId")
    List<Project> findAllProjectsOwnedByTheEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT p FROM Project p JOIN p.owner e WHERE e.team.id = :teamId")
    List<Project> findAllProjectsOwnedByTeamMembers(@Param("teamId") Long teamId);

    Optional<Project> findById(Long projectId);
}