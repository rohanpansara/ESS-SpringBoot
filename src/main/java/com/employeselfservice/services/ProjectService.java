package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
import com.employeselfservice.repositories.EmployeeRepository;
import com.employeselfservice.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Project> findAllProjects(){
        return projectRepository.findAll();
    }

    public List<Project> getAllProjectsOwnedByTheEmployee(Long employeeId, String status) {
        List<Project> projectList = projectRepository.findAllProjectsOwnedByTheEmployee(employeeId);

        // filtering projects based on status
        if (status != null && !status.isEmpty()) {

            if(status.equals("ALL")){
                return projectList;
            }

            Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());
            projectList = projectList.stream()
                    .filter(project -> project.getStatus() == projectStatus)
                    .collect(Collectors.toList());
        }

        return projectList;
    }

    public Project addProject(Project project){
        return projectRepository.save(project);
    }

    public boolean deleteProject(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            projectRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getProjectsAssignedToTheEmployee(long id) {
        return projectRepository.findAllProjectsAssignedToTheEmployeeId(id);
    }

    public Project findProjectById(Long id){
        return projectRepository.findById(id).get();
    }

    public long getNumberOfProjects(){
        return projectRepository.count();
    }
}