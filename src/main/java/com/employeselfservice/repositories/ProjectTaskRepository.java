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

}