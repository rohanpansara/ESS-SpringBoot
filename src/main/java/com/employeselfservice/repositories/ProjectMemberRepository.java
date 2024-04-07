package com.employeselfservice.repositories;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Long> {
    // Method to find all employees working on a particular project
    @Query("SELECT pm.employee FROM ProjectMember pm WHERE pm.project.id = :projectId")
    List<Employee> findAllEmployeesByProjectId(@Param("projectId") Long projectId);

}