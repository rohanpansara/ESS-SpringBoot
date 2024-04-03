package com.employeselfservice.services;

import com.employeselfservice.models.Project;
import com.employeselfservice.repositories.EmployeeRepository;
import com.employeselfservice.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Project> findAllProjectsForEmployee(Long employeeId){
        return projectRepository.findAllProjectsOwnedByTheEmployee(employeeId);
    }

    public Project addProject(Project project){
        return projectRepository.save(project);
    }

    public List<Project> getProjectsAssignedToTheEmployee(long id) {
        return projectRepository.findAllProjectsAssignedToTheEmployeeId(id);
    }
}