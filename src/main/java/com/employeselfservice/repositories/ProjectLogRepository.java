package com.employeselfservice.repositories;

import com.employeselfservice.models.ProjectLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectLogRepository extends JpaRepository<ProjectLog,Long> {

    @Query("SELECT pl FROM ProjectLog pl WHERE pl.project.id = :projectId")
    List<ProjectLog> findAllLogsByProjectId(@Param("projectId") Long projectId);

}
