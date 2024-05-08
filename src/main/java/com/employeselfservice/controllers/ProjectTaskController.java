package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.request.AddProjectTaskRequestDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
import com.employeselfservice.models.Employee;
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
    private ApiResponseDTO apiResponseDTO;

    @GetMapping("/getAllTaskForEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> getAllTaskForEmployee(@RequestParam long id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            List<ProjectTask> projectTaskList = projectTaskService.findAllTasksAssignedToTeam(employee.getTeam().getId(), id);
            if(!projectTaskList.isEmpty()){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Tasks Fetched For A Particular Project Of All Team Members");
                apiResponseDTO.setData(projectTaskList);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("empty");
                apiResponseDTO.setData(projectService.findProjectById(id));
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

    @PutMapping("/updateStatus/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> updateTaskStatus(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id, @RequestParam("status") ProjectTask.TaskStatus status) {

        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            if (projectTaskService.updateTaskStatus(id, status)) {
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Task Status Updated");
                apiResponseDTO.setData(null);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Couldn't Update Task");
                apiResponseDTO.setData(null);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @PostMapping("/addTask")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> addTask(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("projectId") Long projectId, @RequestBody AddProjectTaskRequestDTO addProjectTaskRequestDTO) {
        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            ProjectTask projectTask = projectTaskService.addTask(addProjectTaskRequestDTO, projectId);
            if(projectTask!=null){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Task Added Successfully");
                apiResponseDTO.setData(null);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Couldn't Add Task");
                apiResponseDTO.setData(null);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }
}