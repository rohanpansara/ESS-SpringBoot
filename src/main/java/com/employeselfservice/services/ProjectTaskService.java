package com.employeselfservice.services;

import com.employeselfservice.dto.request.ProjectTaskRequest;
import com.employeselfservice.models.Project;
import com.employeselfservice.models.ProjectMember;
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
    private ProjectService projectService;

    @Autowired
    private ProjectMemberService projectMemberService;

    public List<ProjectTask> getAllTasks(Long projectId){
        return projectTaskRepository.findAllTasksByProjectId(projectId);
    }

    public List<ProjectTask> getAllTaskForEmployee(Long employeeId){
        return projectTaskRepository.findAllTasksByEmployeeId(employeeId);
    }

    public List<ProjectTask> getAllTaskDoneByEmployee(Long employeeId){
        return projectTaskRepository.findAllTasksByEmployeeIdAndStatusDone(employeeId);
    }

    public List<ProjectTask> findAllTasksAssignedToTeam(Long teamId, Long projectId) {
        return projectTaskRepository.findAllTasksAssignedToTeamAndProject(teamId, projectId);
    }

    public boolean updateTaskStatus(Long taskId, ProjectTask.TaskStatus status) {
        int rowsAffected = projectTaskRepository.updateTaskStatusById(taskId, status);
        return rowsAffected > 0;
    }

    public ProjectTask addTask(ProjectTaskRequest projectTaskRequest, Long projectId){
        ProjectTask projectTask = new ProjectTask();

        Project taskProject = projectService.findProjectById(projectId);
        ProjectMember taskMember = projectMemberService.findProjectMember(projectTaskRequest.getAssignedToId());

        projectTask.setProject(taskProject);
        projectTask.setProjectMember(taskMember);
        projectTask.setDescription(projectTaskRequest.getTaskDescription());
        projectTask.setCreatedOn(LocalDate.now());
        projectTask.setStartDate(projectTaskRequest.getStartDate());
        projectTask.setEndDate(projectTaskRequest.getEndDate());
        projectTask.setStatus(projectTaskRequest.getStatus());
        projectTask.setType(projectTaskRequest.getType());
        projectTask.setPriority(projectTaskRequest.getPriority());

        System.out.println(projectTask);

        return projectTaskRepository.save(projectTask);
    }

    public long getNumberOfTasks(){
        return projectTaskRepository.count();
    }

}