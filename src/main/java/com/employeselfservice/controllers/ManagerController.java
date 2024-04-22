package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.LeaveDTO;
import com.employeselfservice.dto.TeamMemberDAO;
import com.employeselfservice.dto.request.AddProjectMemberRequest;
import com.employeselfservice.dto.request.ProjectRequest;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.*;
import com.employeselfservice.services.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/manager")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class ManagerController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private TeamService teamService;

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

    @PostMapping("/project/addProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> addProject(@RequestBody ProjectRequest projectRequest, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            Project project = new Project();

            project.setOwner(employee);
            project.setName(projectRequest.getProjectName());
            project.setKey(projectRequest.generateKey(projectRequest.getProjectName()));
            project.setCreatedOn(LocalDate.now());
            project.setInitiation(projectRequest.getProjectInitiation());
            project.setDeadline(projectRequest.getProjectDeadline());
            if (!projectRequest.getProjectDescription().equals("")) {
                project.setDescription(projectRequest.getProjectDescription());
            } else {
                project.setDescription("Project Created at " + LocalDateTime.now());
            }
            project.setProgress(0);
            project.setStatus(projectRequest.getProjectStatus());
            project.setLastActivity(LocalDate.now());

            if (projectService.addProject(project) != null) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Project Added");
                apiResponse.setData(project);
                return ResponseEntity.ok(apiResponse);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Add Project In Our Database");
                apiResponse.setData(null);
                return ResponseEntity.badRequest().body(apiResponse);
            }
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

    @PutMapping("/project/updateProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> updateProject(@RequestParam("projectId") Long id, @RequestBody Map<String, Object> updates) {
        Project project = projectService.findProjectById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "id":
                    break;
                case "projectName":
                    if (value instanceof String) {
                        project.setName((String) value);
                    }
                    break;
                case "projectDescription":
                    if (value instanceof String) {
                        project.setDescription((String) value);
                    }
                    break;
                case "projectStatus":
                    if (value instanceof String) {
                        // Convert the string to ProjectStatus enum
                        Project.ProjectStatus status = Project.ProjectStatus.valueOf((String) value);
                        project.setStatus(status);
                    }
                    break;
                case "projectInitiation":
                    if (value instanceof String) {
                        // Convert the string to LocalDate
                        project.setInitiation(LocalDate.parse((String) value));
                    }
                    break;
                case "projectDeadline":
                    if (value instanceof String) {
                        // Convert the string to LocalDate
                        project.setDeadline(LocalDate.parse((String) value));
                    }
                    break;
                default:
                    System.out.println("Error Updating: No Case Found for " + key);
                    break;
            }
        });

        // Save the updated project entity
        Project updatedProject = projectService.updateProject(project);
        if (updatedProject!=null) {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Project Details Updated");
            apiResponse.setData(null);
        } else {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Couldn't Update Project Details");
            apiResponse.setData(project);
        }
        return ResponseEntity.ok(apiResponse);
    }


    @DeleteMapping("/project/deleteProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> deleteProject(@RequestParam("id") Long projectId) {
        try {
            if (projectService.deleteProject(projectId)) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Project Deleted Successfully");
                apiResponse.setData(null);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Delete Project");
                apiResponse.setData(null);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/project/getProjectsOwnedByTheEmployee")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getProjectOwnedByTheEmployee(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("status") String status) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Project> projectList = projectService.getAllProjectsOwnedByTheEmployee(employee.getId(),status);
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
                apiResponse.setMessage("Members Added To "+project.getName().toUpperCase());
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
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            Team team = employeeService.checkForManager(employee.getId());

            if (team == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Team Not Found!");
                apiResponse.setData(null);
                return ResponseEntity.badRequest().body(apiResponse);
            } else {
                List<Leave> leaves = leaveService.getAllLeavesByTeam(team);
                List<LeaveDTO> leaveDTOs = convertToDTOs(leaves); // Convert to DTOs
                if (!leaves.isEmpty()) {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("All leaves fetched for Team-" + team.getName());
                    apiResponse.setData(leaveDTOs); // Set DTOs in response
                } else {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("No pending leave applications for the team");
                    apiResponse.setData(leaveDTOs); // Set DTOs in response
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

    private List<LeaveDTO> convertToDTOs(List<Leave> leaves) {
        return leaves.stream()
                .map(leave -> new LeaveDTO(
                        leave.getEmployee().getFirstname() + " " + leave.getEmployee().getLastname(),
                        leave.getAppliedOn(),
                        leave.getReason(),
                        leave.getFrom(),
                        leave.getTo(),
                        leave.getType(),
                        leave.getOverflow(),
                        leave.getStatus()))
                .collect(Collectors.toList());
    }

    @GetMapping("/leaves/getAllLeavesToBeApproved")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllLeavesToBeApproved(@RequestHeader("Authorization") String authorizationHeader) {
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
                List<Leave> leaves = leaveService.findAllPendingLeavesByTeam(team);
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

    @GetMapping("/employee/getMembersToAssignProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getMembersToAssignProject(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("projectId") Long projectId){
        try{
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Employee> employeeList = employeeService.findEmployeesInTeamExcludingDesignation(employee.getTeam(),projectId);
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

    @GetMapping("/team/getAllTeamMembers")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllMembersOfTheTeam(@RequestHeader("Authorization") String authorizationHeader){
        try{
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<TeamMemberDAO> employeeList = teamService.getTeamMembersDetailsOfManager(employee.getTeam().getId());
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
