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

    @Query("SELECT DISTINCT p FROM Project p JOIN p.projectMembers pm WHERE pm.employee.id = :employeeId")
    List<Project> findAllProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT DISTINCT pm.employee FROM ProjectMember pm WHERE pm.project.id = :projectId")
    List<Employee> findAllEmployeesByProjectId(@Param("projectId") Long projectId);
}
