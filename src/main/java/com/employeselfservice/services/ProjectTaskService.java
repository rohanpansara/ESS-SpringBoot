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

    public List<ProjectTask> getAllTaskForEmployee(Long employeeId){
        return projectTaskRepository.findAllTasksByEmployeeId(employeeId);
    }
}