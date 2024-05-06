package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.ProjectMemberService;
import com.employeselfservice.services.ProjectService;
import com.employeselfservice.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/user/project")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class ProjectController {

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

    @GetMapping("/getProjectsAssignedToTheEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getProjectsAssignedToTheEmployee(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("status") String status) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Project> projectList = projectService.getProjectsAssignedToTheEmployee(employee.getId());

            // Check if the status parameter is null or empty
            if (status == null || status.isEmpty() || status.equalsIgnoreCase("ALL")) {
                return ResponseEntity.ok(new ApiResponse(true, "All projects fetched successfully", projectList));
            }

            // Convert the provided status string to ProjectStatus enum
            Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());

            // Filter projects based on the provided status
            List<Project> filteredProjects = projectList.stream()
                    .filter(project -> project.getStatus() == projectStatus)
                    .collect(Collectors.toList());

            if(projectList.isEmpty()){
                return ResponseEntity.ok(new ApiResponse(true, "No Projects Found", filteredProjects));
            } else if(filteredProjects.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(true, "No Projects Found With Status: "+status, filteredProjects));
            } else{
                return ResponseEntity.ok(new ApiResponse(true, "Projects Fetched Successfully!: ", filteredProjects));
            }
        } catch (IllegalArgumentException e) {
            // If the provided status string doesn't match any enum value
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid status: " + status, null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Employee not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Error: " + e.getMessage(), null));
        }
    }


    @GetMapping("/getProjectMembers")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllProjectMembers(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("projectId") Long projectId) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));


            //check if the current employee is a member of the project (again)
            //implementation left


            List<Employee> projectMemberList = projectMemberService.findAllProjectMembers(projectId);
            if (projectMemberList.isEmpty()) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("No Members Assigned");
                apiResponse.setData(projectMemberList);
            } else {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Project Members Fetched");
                apiResponse.setData(projectMemberList);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid Project ID format", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Project not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Error: " + e.getMessage(), null));
        }
    }
}
