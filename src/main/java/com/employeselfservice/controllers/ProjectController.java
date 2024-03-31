package com.employeselfservice.controllers;

import com.employeselfservice.dao.request.ProjectRequest;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
import com.employeselfservice.services.EmployeeService;
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
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class ProjectController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/user/project/addProject")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> addProject(@RequestBody ProjectRequest projectRequest) {
        try {
            Project project = new Project();

            project.setCreatedOn(LocalDate.now());
            project.setName(projectRequest.getProjectName());
            project.setKey(projectRequest.generateKey(projectRequest.getProjectName()));
            project.setDeadline(projectRequest.getProjectDeadline());
            if(!projectRequest.getProjectDescription().equals("")){
                project.setDescription(projectRequest.getProjectDescription());
            }
            else{
                project.setDescription("Project Created");
            }
            project.setOwner(new Employee(projectRequest.getOwnerId()));
            project.setProgress(0);
            project.setInitiation(projectRequest.getProjectInitiation());
            project.setStatus(Project.ProjectStatus.NEW);

            projectService.addProject(project);
            if(project!=null){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Project Added");
                apiResponse.setData(project);
                return ResponseEntity.ok(apiResponse);
            }
            else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Add Project In The Database");
                apiResponse.setData(null);
                return ResponseEntity.badRequest().body(apiResponse);
            }
        } catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: "+e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @GetMapping("/user/project/getProjectForEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getProjectForEmployee(@RequestParam long id) {
        try {
            List<Project> projectList = projectService.findAllProjectsForEmployee(id);
            if(projectList.isEmpty()){
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No Projects Found");
                apiResponse.setData(projectList);
            }
            else{
                apiResponse.setSuccess(true);
                apiResponse.setMessage("All Projects Fetched");
                apiResponse.setData(projectList);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (NumberFormatException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid employee ID format");
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
        } catch (NoSuchElementException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Employee not found with ID: " + id);
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
