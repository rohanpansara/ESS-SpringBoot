package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.AdminDashboardWidgetsDAO;
import com.employeselfservice.dto.EmployeeDAO;
import com.employeselfservice.dto.TeamDTO;
import com.employeselfservice.dto.request.AddTeamRequest;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Team;
import com.employeselfservice.services.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth/admin")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AdminDashboardController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AdminDashboardWidgetsDAO adminDashboardWidgetsDao;

    @Autowired
    private EmployeeDAO employeeDAO;


    @GetMapping("/getCurrentAdminDetails")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getCurrentAdminDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee admin = employeeService.findByEmail(jwtService.extractUsername(token));

            employeeDAO.setId(admin.getId());
            employeeDAO.setName(admin.getFirstname() + " " + admin.getLastname());
            employeeDAO.setDesignation(admin.getDesignation());
            employeeDAO.setTeam(admin.getTeam());
            employeeDAO.setRole(admin.getRoles());

            apiResponse.setSuccess(true);
            apiResponse.setMessage("Admin Data Fetched!");
            apiResponse.setData(employeeDAO);
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

    @GetMapping("/getDashboardWidgetData")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getDashboardWidgetData() {
        try {
            adminDashboardWidgetsDao.setNumberOfProjects(projectService.getNumberOfProjects());
            adminDashboardWidgetsDao.setNumberOfEmployees(employeeService.getNumberOfEmployee());
            adminDashboardWidgetsDao.setNumberOfPendingLeaveRequests(leaveService.getNumberOfPendingLeaves());

            adminDashboardWidgetsDao.setNumberOfTasks(projectTaskService.getNumberOfTasks());
            adminDashboardWidgetsDao.setAverageWorkHours(attendanceService.calculateAverageWorkHours());
            adminDashboardWidgetsDao.setNumberOfApprovedLeaveRequests(leaveService.getNumberOfApprovedLeaves());

            apiResponse.setSuccess(true);
            apiResponse.setMessage("Admin Data Fetched!");
            apiResponse.setData(adminDashboardWidgetsDao);
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

    @GetMapping("/team/getAllTeams")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllTeams(){
        try {
            List<Team> teamList = teamService.getAllTeams();
            List<TeamDTO> teamDTOList = mapTeamListToDTO(teamList);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("All Teams Fetched");
            apiResponse.setData(teamDTOList);
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

    // Mapper method to convert Team objects to TeamDTO objects
    private List<TeamDTO> mapTeamListToDTO(List<Team> teamList) {
        List<TeamDTO> teamDTOList = new ArrayList<>();
        for (Team team : teamList) {
            TeamDTO teamDTO = new TeamDTO();
            teamDTO.setTeamId(team.getId());
            teamDTO.setTeamName(team.getName());
            teamDTO.setTeamMembers(team.getEmployees().size()); // Assuming team members count is based on the size of the employees list

            // Fetch Manager name from designations
            String managerName = fetchManagerName(team);
            teamDTO.setManagerName(managerName);

            // Fetch Tech Lead name from designations
            String techLeadName = fetchTechLead(team);
            teamDTO.setTechLeadName(techLeadName);

            teamDTOList.add(teamDTO);
        }
        return teamDTOList;
    }

    // Method to fetch Manager name from designations
    private String fetchManagerName(Team team) {
        String managerName = "";
        for (Employee employee : team.getEmployees()) {
            if (employee.getDesignation() != null && employee.getDesignation().getName().equals("Manager")) {
                managerName = employee.getFirstname() + " " + employee.getLastname();
                break; // No need to continue searching
            }
        }
        return managerName;
    }

    // Method to fetch Tech Lead name from designations
    private String fetchTechLead(Team team) {
        String managerName = "";
        for (Employee employee : team.getEmployees()) {
            if (employee.getDesignation() != null && employee.getDesignation().getName().equals("Tech Lead")) {
                managerName = employee.getFirstname() + " " + employee.getLastname();
                break;
            }
        }
        return managerName;
    }


    @PostMapping("/addNewTeam")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> addTeam(@RequestBody AddTeamRequest addTeamRequest) {
        try {
            if (teamService.addTeam(addTeamRequest)) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Team Added Successfully");
                apiResponse.setData(null);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Add Team");
                apiResponse.setData(null);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
