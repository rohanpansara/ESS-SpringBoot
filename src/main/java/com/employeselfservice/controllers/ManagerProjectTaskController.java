package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.ApiResponseDTO;
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
    private ApiResponseDTO apiResponseDTO;

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
    public ResponseEntity<ApiResponseDTO> getAllTasksForAProject(@RequestParam("projectId") Long projectId){
        try {
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
