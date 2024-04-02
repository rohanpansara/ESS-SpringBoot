package com.employeselfservice.services;

import com.employeselfservice.models.ProjectTask;
import com.employeselfservice.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public List<ProjectTask> getAllTasksForTheEmployee(Long employeeId){
        return projectTaskRepository.findAllTasksByEmployeeId(employeeId);
    }

    public List<ProjectTask> getAllTasksInTheProject(Long projectId){
        return projectTaskRepository.findAllTasksByProjectId(projectId);
    }
}
