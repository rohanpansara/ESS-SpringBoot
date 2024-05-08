package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.LeaveDTO;
import com.employeselfservice.dto.response.TeamMemberDTO;
import com.employeselfservice.dto.request.AddProjectMemberRequestDTO;
import com.employeselfservice.dto.request.AddProjectRequestDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
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
    private ApiResponseDTO apiResponseDTO;

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
    public ResponseEntity<ApiResponseDTO> addProject(@RequestBody AddProjectRequestDTO addProjectRequestDTO, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            Project project = new Project();

            project.setOwner(employee);
            project.setName(addProjectRequestDTO.getProjectName());
            project.setKey(addProjectRequestDTO.generateKey(addProjectRequestDTO.getProjectName()));
            project.setCreatedOn(LocalDate.now());
            project.setInitiation(addProjectRequestDTO.getProjectInitiation());
            project.setDeadline(addProjectRequestDTO.getProjectDeadline());
            if (!addProjectRequestDTO.getProjectDescription().equals("")) {
                project.setDescription(addProjectRequestDTO.getProjectDescription());
            } else {
                project.setDescription("Project Created at " + LocalDateTime.now());
            }
            project.setProgress(0);
            project.setStatus(addProjectRequestDTO.getProjectStatus());
            project.setLastActivity(LocalDate.now());

            if (projectService.addProject(project) != null) {
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Project Added");
                apiResponseDTO.setData(project);
                return ResponseEntity.ok(apiResponseDTO);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Couldn't Add Project In Our Database");
                apiResponseDTO.setData(null);
                return ResponseEntity.badRequest().body(apiResponseDTO);
            }
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

    @PutMapping("/project/updateProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> updateProject(@RequestParam("projectId") Long id, @RequestBody Map<String, Object> updates) {
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
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Project Details Updated");
            apiResponseDTO.setData(null);
        } else {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Couldn't Update Project Details");
            apiResponseDTO.setData(project);
        }
        return ResponseEntity.ok(apiResponseDTO);
    }


    @DeleteMapping("/project/deleteProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> deleteProject(@RequestParam("id") Long projectId) {
        try {
            if (projectService.deleteProject(projectId)) {
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Project Deleted Successfully");
                apiResponseDTO.setData(null);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Couldn't Delete Project");
                apiResponseDTO.setData(null);
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/project/getProjectsOwnedByTheEmployee")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> getProjectOwnedByTheEmployee(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("status") String status) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Project> projectList = projectService.getAllProjectsOwnedByTheEmployee(employee.getId(),status);
            if(projectList.isEmpty()){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("No Projects Found With Status: "+status);
                apiResponseDTO.setData(projectList);
            } else {
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("All Projects Owned By Employee With ID: " + employee.getId() + " are Fetched");
                apiResponseDTO.setData(projectList);
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (AccessDeniedException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDTO(false,"Access Denied",null));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDTO(false, "Token Error"+e.getMessage(), null));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ApiResponseDTO(false, "Invalid employee ID format", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDTO(false, "Employee not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO(false, "Internal Error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/project/addProjectMembers")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> addProjectMembers(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AddProjectMemberRequestDTO projectMemberRequest){
        try{

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee projectManager = employeeService.findByEmail(jwtService.extractUsername(token));

            Project project = projectService.findProjectById(projectMemberRequest.getProjectId());
            if (projectMemberService.addProjectMembers(project,projectMemberRequest.getMembers(),projectManager)){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Members Added To "+project.getName().toUpperCase());
                apiResponseDTO.setData(null);
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again!");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        } catch (AccessDeniedException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Either your token expired or You are not logged in: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseDTO);
        } catch (DataAccessException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Database access error occurred while fetching leave applications: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/leaves/getAllLeave")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> getAllLeavesForManager(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            Team team = employeeService.checkForManager(employee.getId());

            if (team == null) {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Team Not Found!");
                apiResponseDTO.setData(null);
                return ResponseEntity.badRequest().body(apiResponseDTO);
            } else {
                List<Leave> leaves = leaveService.getAllLeavesByTeam(team);
                List<LeaveDTO> leaveDTOs = convertToDTOs(leaves); // Convert to DTOs
                if (!leaves.isEmpty()) {
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("All leaves fetched for Team-" + team.getName());
                    apiResponseDTO.setData(leaveDTOs); // Set DTOs in response
                } else {
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("No pending leave applications for the team");
                    apiResponseDTO.setData(leaveDTOs); // Set DTOs in response
                }
                return ResponseEntity.ok(apiResponseDTO);
            }
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again!");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        } catch (AccessDeniedException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Either your token expired or You are not logged in: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseDTO);
        } catch (DataAccessException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Database access error occurred while fetching leave applications: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
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
    public ResponseEntity<ApiResponseDTO> getAllLeavesToBeApproved(@RequestHeader("Authorization") String authorizationHeader) {
        try{
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            Team team = employeeService.checkForManager(employee.getId());

            if(team==null){
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Team Not Found!");
                apiResponseDTO.setData(null);
                return ResponseEntity.badRequest().body(apiResponseDTO);
            }
            else{
                List<Leave> leaves = leaveService.findAllPendingLeavesByTeam(team);
                System.out.println(leaves);
                if(!leaves.isEmpty()){
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("All leaves fetched for Team-"+team.getName());
                    apiResponseDTO.setData(leaves);
                }
                else{
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("No pending leave applications for the team");
                    apiResponseDTO.setData(leaves);
                }
                return ResponseEntity.ok(apiResponseDTO);
            }
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again!");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        } catch (AccessDeniedException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Either your token expired or You are not logged in: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseDTO);
        } catch (DataAccessException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Database access error occurred while fetching leave applications: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }


    @PutMapping("/leaves/updateStatus")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> approveLeave(@RequestParam int id, @RequestParam String status){
        try {
            String leaveResponse = leaveService.approveLeave(id,status);
            switch (leaveResponse) {
                case "Approved" -> {
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("Leave Approved");
                    apiResponseDTO.setData(null);
                }
                case "Rejected" -> {
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("Leave Rejected");
                    apiResponseDTO.setData(null);
                }
                case "Leave_Not_Found" -> {
                    apiResponseDTO.setSuccess(false);
                    apiResponseDTO.setMessage("No Such Leave Application Found!");
                    apiResponseDTO.setData(null);
                }
                default -> {
                    apiResponseDTO.setSuccess(false);
                    apiResponseDTO.setMessage("Something Went Wrong!");
                    apiResponseDTO.setData(null);
                }
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again!");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/employee/getMembersToAssignProject")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> getMembersToAssignProject(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("projectId") Long projectId){
        try{
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Employee> employeeList = employeeService.findEmployeesInTeamExcludingDesignation(employee.getTeam(),projectId);
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("All Members Under "+employee.getFirstname()+" Are Fetched!");
            apiResponseDTO.setData(employeeList);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again!");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/team/getAllTeamMembers")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiResponseDTO> getAllMembersOfTheTeam(@RequestHeader("Authorization") String authorizationHeader){
        try{
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<TeamMemberDTO> employeeList = teamService.getTeamMembersDetailsOfManager(employee.getTeam().getId());
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("All Members Under "+employee.getFirstname()+" Are Fetched!");
            apiResponseDTO.setData(employeeList);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (ExpiredJwtException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Token Expired. Login Again!");
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
