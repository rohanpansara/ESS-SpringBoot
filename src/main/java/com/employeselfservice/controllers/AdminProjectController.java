package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/admin/project/")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AdminProjectController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectTaskService projectTaskService;

    @GetMapping("/getAllProjects")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllProjects(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("status") String status) {
        try {
            List<Project> projectList = projectService.findAllProjects();

            // Check if the status parameter is null or empty
            if (status == null || status.isEmpty() || status.equalsIgnoreCase("ALL")) {
                return ResponseEntity.ok(new ApiResponse(true, "All projects fetched successfully", projectList));
            }

            // convert the provided status string to ProjectStatus enum
            Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());

            // Filter projects based on the provided status
            List<Project> filteredProjects = projectList.stream()
                    .filter(project -> project.getStatus() == projectStatus)
                    .collect(Collectors.toList());

            if (projectList.isEmpty()) {
                apiResponse.setMessage("No Projects Created");
            } else {
                apiResponse.setMessage("All Projects Fetched");
            }
            apiResponse.setSuccess(true);
            apiResponse.setData(filteredProjects);
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

    @GetMapping("/task/getAllTask")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllTaskForEmployee(@RequestParam("projectId") long projectId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

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
