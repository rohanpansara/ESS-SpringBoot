package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.request.AddProjectMemberRequest;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Leave;
import com.employeselfservice.models.Project;
import com.employeselfservice.models.Team;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.LeaveService;
import com.employeselfservice.services.ProjectMemberService;
import com.employeselfservice.services.ProjectService;
import com.employeselfservice.services.ProjectTaskService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth/manager")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class ManagerController {

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

    @Autowired
    private LeaveService leaveService;

    @GetMapping("/project/getProjectsOwnedByTheEmployee")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
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

    @PostMapping("/project/addProjectMembers")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> addProjectMembers(@RequestBody AddProjectMemberRequest projectMemberRequest){
        try{
            Project project = projectService.findProjectById(projectMemberRequest.getProjectId());
            if (projectMemberService.addProjectMembers(project,projectMemberRequest.getMembers())){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Project Members Added To The Project--"+project.getName().toUpperCase());
                apiResponse.setData(null);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again!");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (AccessDeniedException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Either your token expired or You are not logged in: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        } catch (DataAccessException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Database access error occurred while fetching leave applications: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/leaves/getAllLeave")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllLeavesForManager(@RequestHeader("Authorization") String authorizationHeader) {
        try{
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            Team team = employeeService.checkForManager(employee.getId());

            if(team==null){
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Team Not Found!");
                apiResponse.setData(null);
                return ResponseEntity.badRequest().body(apiResponse);
            }
            else{
                List<Leave> leaves = leaveService.findAllApprovedLeavesByTeam(team);
                System.out.println(leaves);
                if(!leaves.isEmpty()){
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("All leaves fetched for Team-"+team.getName());
                    apiResponse.setData(leaves);
                }
                else{
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("No pending leave applications for the team");
                    apiResponse.setData(leaves);
                }
                return ResponseEntity.ok(apiResponse);
            }
        } catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again!");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (AccessDeniedException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Either your token expired or You are not logged in: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        } catch (DataAccessException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Database access error occurred while fetching leave applications: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PutMapping("/leaves/updateStatus")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> approveLeave(@RequestParam int id, @RequestParam String status){
        try {
            String leaveResponse = leaveService.approveLeave(id,status);
            switch (leaveResponse) {
                case "Approved" -> {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Leave Approved");
                    apiResponse.setData(null);
                }
                case "Rejected" -> {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Leave Rejected");
                    apiResponse.setData(null);
                }
                case "Leave_Not_Found" -> {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("No Such Leave Application Found!");
                    apiResponse.setData(null);
                }
                default -> {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("Something Went Wrong!");
                    apiResponse.setData(null);
                }
            }
            return ResponseEntity.ok(apiResponse);
        } catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again!");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/employee/getAllTeamMembers")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllMembersOfTheTeam(@RequestHeader("Authorization") String authorizationHeader){
        try{
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Employee> employeeList = employeeService.findEmployeesInTeamExcludingDesignation(employee.getTeam());
            apiResponse.setSuccess(true);
            apiResponse.setMessage("All Members Under "+employee.getFirstname()+" Are Fetched!");
            apiResponse.setData(employeeList);
            return ResponseEntity.ok(apiResponse);
        } catch (ExpiredJwtException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Token Expired. Login Again!");
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
