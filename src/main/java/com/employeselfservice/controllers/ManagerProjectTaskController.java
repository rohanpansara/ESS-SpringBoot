package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.ProjectTask;
import com.employeselfservice.services.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/manager/project/task")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class ManagerProjectTaskController {
    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/getAllTasksForTheProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllTasksForAProject(@RequestParam("projectId") Long projectId){
        try {
            List<ProjectTask> projectTaskList = projectTaskService.getAllTasks(projectId);
            if(!projectTaskList.isEmpty()){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Tasks Fetched For A Particular Project Of All Members");
                apiResponse.setData(projectTaskList);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("empty");
                apiResponse.setData(projectService.findProjectById(projectId));
            }
            return ResponseEntity.ok(apiResponse);
        } catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
