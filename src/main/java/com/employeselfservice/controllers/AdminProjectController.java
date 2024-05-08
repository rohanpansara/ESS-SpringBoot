package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.ApiResponseDTO;
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
    private ApiResponseDTO apiResponseDTO;

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
    public ResponseEntity<ApiResponseDTO> getAllProjects(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("status") String status) {
        try {
            List<Project> projectList = projectService.findAllProjects();

            // Check if the status parameter is null or empty
            if (status == null || status.isEmpty() || status.equalsIgnoreCase("ALL")) {
                return ResponseEntity.ok(new ApiResponseDTO(true, "All projects fetched successfully", projectList));
            }

            // convert the provided status string to ProjectStatus enum
            Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());

            // Filter projects based on the provided status
            List<Project> filteredProjects = projectList.stream()
                    .filter(project -> project.getStatus() == projectStatus)
                    .collect(Collectors.toList());

            if (projectList.isEmpty()) {
                apiResponseDTO.setMessage("No Projects Created");
            } else {
                apiResponseDTO.setMessage("All Projects Fetched");
            }
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setData(filteredProjects);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/task/getAllTask")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> getAllTaskForEmployee(@RequestParam("projectId") long projectId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            List<ProjectTask> projectTaskList = projectTaskService.getAllTasks(projectId);
            if(!projectTaskList.isEmpty()){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Tasks Fetched For A Particular Project Of All Members");
                apiResponseDTO.setData(projectTaskList);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("empty");
                apiResponseDTO.setData(projectService.findProjectById(projectId));
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.badRequest().body(apiResponseDTO);
        }
    }
}
