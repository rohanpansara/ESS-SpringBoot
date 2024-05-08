package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.request.AddTeamRequestDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
import com.employeselfservice.dto.response.EmployeeDTO;
import com.employeselfservice.dto.response.TeamDTO;
import com.employeselfservice.dto.response.WidgetsDTO;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Team;
import com.employeselfservice.services.AttendanceService;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.LeaveService;
import com.employeselfservice.services.ProjectService;
import com.employeselfservice.services.ProjectTaskService;
import com.employeselfservice.services.TeamService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth/admin")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AdminDashboardController {

    @Autowired
    private ApiResponseDTO apiResponseDTO;

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
    private WidgetsDTO widgetsDTO;

    @Autowired
    private EmployeeDTO employeeDTO;


    @GetMapping("/getCurrentAdminDetails")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> getCurrentAdminDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee admin = employeeService.findByEmail(jwtService.extractUsername(token));

            employeeDTO.setId(admin.getId());
            employeeDTO.setName(admin.getFirstname() + " " + admin.getLastname());
            employeeDTO.setDesignation(admin.getDesignation());
            employeeDTO.setTeam(admin.getTeam());
            employeeDTO.setRole(admin.getRoles());

            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Admin Data Fetched!");
            apiResponseDTO.setData(employeeDTO);
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

    @GetMapping("/getDashboardWidgetData")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> getDashboardWidgetData() {
        try {

            widgetsDTO.setWidgetPrimaryOne(String.valueOf(projectService.getNumberOfProjects()));
            widgetsDTO.setWidgetSecondaryOne(String.valueOf(projectTaskService.getNumberOfTasks()));

            widgetsDTO.setWidgetPrimaryTwo(String.valueOf(employeeService.getNumberOfEmployee()));
            widgetsDTO.setWidgetSecondaryTwo(attendanceService.calculateAverageWorkHours());

            widgetsDTO.setWidgetPrimaryThree(String.valueOf(leaveService.getNumberOfPendingLeaves()));
            widgetsDTO.setWidgetSecondaryThree(String.valueOf(leaveService.getNumberOfApprovedLeaves()));

            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Admin Data Fetched!");
            apiResponseDTO.setData(widgetsDTO);
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

    @GetMapping("/team/getAllTeams")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> getAllTeams(){
        try {
            List<Team> teamList = teamService.getAllTeams();
            List<TeamDTO> teamDTOList = mapTeamListToDTO(teamList);
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("All Teams Fetched");
            apiResponseDTO.setData(teamDTOList);
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
    public ResponseEntity<ApiResponseDTO> addTeam(@RequestBody AddTeamRequestDTO addTeamRequestDTO) {
        try {
            if (teamService.addTeam(addTeamRequestDTO)) {
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Team Added Successfully");
                apiResponseDTO.setData(null);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Couldn't Add Team");
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
