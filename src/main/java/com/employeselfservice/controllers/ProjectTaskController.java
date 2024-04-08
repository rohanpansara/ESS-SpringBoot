package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.request.ProjectTaskRequest;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
import com.employeselfservice.models.ProjectMember;
import com.employeselfservice.models.ProjectTask;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.ProjectMemberService;
import com.employeselfservice.services.ProjectService;
import com.employeselfservice.services.ProjectTaskService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/auth/user/project/task")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApiResponse apiResponse;

    @GetMapping("/getAllTaskForEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllTaskForEmployee(@RequestParam long id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            List<ProjectTask> projectTaskList = projectTaskService.findAllTasksAssignedToTeam(employee.getTeam().getId(), id);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Tasks Fetched For A Particular Project Of All Team Members");
            apiResponse.setData(projectTaskList);
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

    @PutMapping("/updateStatus/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> updateTaskStatus(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id, @RequestParam("status") ProjectTask.TaskStatus status) {

        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            if (projectTaskService.updateTaskStatus(id, status)) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Task Status Updated");
                apiResponse.setData(null);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Update Task");
                apiResponse.setData(null);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("/addTask")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> addTask(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("projectId") Long projectId, @RequestBody ProjectTaskRequest projectTaskRequest) {
        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            ProjectTask projectTask = projectTaskService.addTask(projectTaskRequest, projectId);
            if(projectTask!=null){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Task Added Successfully");
                apiResponse.setData(null);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Add Task");
                apiResponse.setData(null);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}