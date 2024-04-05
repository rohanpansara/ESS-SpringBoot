package com.employeselfservice.repositories;
import com.employeselfservice.models.ProjectTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask,Long> {

    @Query("SELECT pt FROM ProjectTask pt WHERE pt.assignedToEmployee.id = :employeeId")
    List<ProjectTask> findAllTasksByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT pt FROM ProjectTask pt WHERE pt.project.id = :projectId")
    List<ProjectTask> findAllTasksByProjectId(@Param("projectId") Long projectId);

    // Method to find all tasks assigned to employees of a specific team for a project
    @Query("SELECT pt FROM ProjectTask pt " +
            "JOIN pt.assignedToEmployee emp " +
            "JOIN emp.team t " +
            "WHERE t.id = :teamId " +
            "AND pt.project.id = :projectId")
    List<ProjectTask> findAllTasksAssignedToTeamAndProject(@Param("teamId") Long teamId, @Param("projectId") Long projectId);
}