package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.request.ProjectRequest;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
import com.employeselfservice.models.ProjectMember;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.ProjectMemberService;
import com.employeselfservice.services.ProjectService;
import com.employeselfservice.services.ProjectTaskService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

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

    @GetMapping("/addProject")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> addProject(@RequestBody ProjectRequest projectRequest, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

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
            project.setOwner(employee);
            project.setProgress(0);
            project.setInitiation(projectRequest.getProjectInitiation());
            project.setStatus(Project.ProjectStatus.NEW);

            if(projectService.addProject(project)!=null){
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

    @GetMapping("/getProjectsOwnedByTheEmployee")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getProjectOwnedByTheEmployee(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Project> projectList = projectService.findAllProjectsForEmployee(employee.getId());
            if(projectList.isEmpty()){
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No Projects Found");
            } else {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("All Projects Owned By Employee With ID: " + employee.getId() + " are Fetched");
            }
            apiResponse.setData(projectList);
            return ResponseEntity.ok(apiResponse);
        } catch (AccessDeniedException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false,"Access Denied",null));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Token Error"+e.getMessage(), null));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiResponse(false, "Invalid employee ID format", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Employee not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/getProjectsAssignedToTheEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getProjectsAssignedToTheEmployee(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));
            List<Project> projectList = projectService.getProjectsAssignedToTheEmployee(employee.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Projects fetched successfully", projectList));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid employee ID format", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "Employee not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Internal Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/getProjectMembers")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllProjectMembers(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("projectId") Long projectId){
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));



            //check if the current employee is a member of the project (again)
            //implementation left



            List<Employee> projectMemberList = projectMemberService.findAllProjectMembers(projectId);
            if(projectMemberList.isEmpty()){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("No Members Assigned");
                apiResponse.setData(projectMemberList);
            } else{
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
