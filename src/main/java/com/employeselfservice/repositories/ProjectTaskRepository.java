package com.employeselfservice.repositories;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.ProjectMember;
import com.employeselfservice.models.ProjectTask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask,Long> {

    // Updated method to find all tasks assigned to an employee by project member
    @Query("SELECT pt FROM ProjectTask pt " +
            "JOIN pt.projectMember pm " +
            "WHERE pm.employee.id = :employeeId")
    List<ProjectTask> findAllTasksByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT pt FROM ProjectTask pt WHERE pt.project.id = :projectId")
    List<ProjectTask> findAllTasksByProjectId(@Param("projectId") Long projectId);

    // Updated method to find all tasks assigned to employees of a specific team for a project
    @Query("SELECT pt FROM ProjectTask pt " +
            "JOIN pt.projectMember pm " +
            "JOIN pm.employee emp " +
            "JOIN emp.team t " +
            "WHERE t.id = :teamId " +
            "AND pt.project.id = :projectId")
    List<ProjectTask> findAllTasksAssignedToTeamAndProject(@Param("teamId") Long teamId, @Param("projectId") Long projectId);

    @Transactional
    @Modifying
    @Query("UPDATE ProjectTask pt SET pt.status = :status WHERE pt.id = :taskId")
    int updateTaskStatusById(@Param("taskId") Long taskId, @Param("status") ProjectTask.TaskStatus status);

//    int countByAssignedToEmployee(Employee employee);

//    @Query("SELECT COUNT(pt) FROM ProjectTask pt WHERE pt.projectMember = :employee AND pt.status = :status")
//    int countByAssignedToEmployeeAndStatus(@Param("employee") ProjectMember employee, @Param("status") ProjectTask.TaskStatus status);

//    int countByAssignedToEmployeeAndStatus(Employee employee, ProjectTask.TaskStatus status);
//    int countByAssignedToEmployeeAndStatus(Employee employee, ProjectTask.TaskStatus status);
}