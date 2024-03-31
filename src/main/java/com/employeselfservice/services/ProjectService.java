package com.employeselfservice.services;

import com.employeselfservice.models.Project;
import com.employeselfservice.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> findAllProjectsForEmployee(Long employeeId){
        return projectRepository.findAllProjectsByEmployeeId(employeeId);
    }

    public Project addProject(Project project){
        return projectRepository.save(project);
    }
}
