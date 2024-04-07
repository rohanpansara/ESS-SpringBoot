package com.employeselfservice.services;

import com.employeselfservice.dao.request.ProjectTaskRequest;
import com.employeselfservice.models.Project;
import com.employeselfservice.models.ProjectTask;
import com.employeselfservice.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectMemberService projectMemberService;


    public List<ProjectTask> getAllTaskForEmployee(Long employeeId){
        return projectTaskRepository.findAllTasksByEmployeeId(employeeId);
    }

    public List<ProjectTask> findAllTasksAssignedToTeam(Long teamId, Long projectId) {
        return projectTaskRepository.findAllTasksAssignedToTeamAndProject(teamId, projectId);
    }

    public boolean updateTaskStatus(Long taskId, ProjectTask.TaskStatus status) {
        int rowsAffected = projectTaskRepository.updateTaskStatusById(taskId, status);
        return rowsAffected > 0;
    }

    public ProjectTask addTask(ProjectTaskRequest projectTaskRequest, Long projectId, Long employeeId){

        ProjectTask projectTask = new ProjectTask();
        projectTask.setProject(new Project(projectId));
        projectTask.setProjectMember(projectMemberService.findProjectMember(employeeId));
        projectTask.setDescription(projectTaskRequest.getTaskDescription());
        projectTask.setCreatedOn(LocalDate.now());
        projectTask.setStartDate(projectTaskRequest.getStartDate());
        projectTask.setEndDate(projectTaskRequest.getEndDate());
        projectTask.setStatus(projectTaskRequest.getStatus());
        projectTask.setType(projectTaskRequest.getType());
        projectTask.setPriority(projectTaskRequest.getPriority());

        return projectTaskRepository.save(projectTask);
    }
}