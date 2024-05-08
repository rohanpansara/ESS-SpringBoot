package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.request.AddEmployeeRequestDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
import com.employeselfservice.models.Designation;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Team;
import com.employeselfservice.services.DesignationService;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.TeamService;
import com.employeselfservice.services.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/admin/employee/")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AdminEmployeeController {

    @Autowired
    private ApiResponseDTO apiResponseDTO;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DesignationService designationService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private AddEmployeeRequestDTO addEmployeeRequestDTO;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @GetMapping("/getAllEmployees")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.findAll();
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("All Employee Records Fetched!");
            apiResponseDTO.setData(employees);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/getTeamsAndDesignations")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> getTeamAndDesignations() {
        try {
            addEmployeeRequestDTO.setDesignations(designationService.getAllDesignations());
            addEmployeeRequestDTO.setTeams(teamService.getAllTeams());

            if (addEmployeeRequestDTO.getDesignations().isEmpty() && addEmployeeRequestDTO.getTeams().isEmpty()) {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("No Teams/Designations Added By Admin Yet");
                apiResponseDTO.setData(null);
            } else {
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("All Teams/Designations Fetched");
                apiResponseDTO.setData(addEmployeeRequestDTO);
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @PostMapping("/addNewEmployee")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> addEmployee(@RequestBody Employee employee) {
        try {
            String firstName = employee.getFirstname();
            String email = employee.getEmail();
            String password = employee.getPassword();

            if (employee.getDesignation().getId() == 1) {
                employee.setRoles("ROLE_MANAGER,ROLE_USER");
            }

            String response = employeeService.addUser(employee);
//            if (response.equals("added")) {
//                System.out.println("About to send email");
//                emailService.sendWelcomeEmail(firstName, email, password);
//            }
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Employee added successfully");
            apiResponseDTO.setData(employee);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @PutMapping("/updateEmployee")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> updateEmployee(@RequestParam("employeeId") Long id, @RequestBody Map<String, Object> updates) {
        Employee employee = employeeService.findEmployeeById(id);

        // Update only the fields that have been changed
        updates.forEach((key, value) -> {
            switch (key) {
                case "id":
                    break;
                case "firstname":
                    if (value instanceof String) {
                        employee.setFirstname((String) value);
                    }
                    break;
                case "middlename":
                    if (value instanceof String) {
                        employee.setMiddlename((String) value);
                    }
                    break;
                case "lastname":
                    if (value instanceof String) {
                        employee.setLastname((String) value);
                    }
                    break;
                case "email":
                    if (value instanceof String) {
                        employee.setEmail((String) value);
                    }
                    break;
                case "password":
                    if (value instanceof String) {
                        employee.setPassword((String) value);
                    }
                    break;
                case "mobile":
                    if (value instanceof String) {
                        employee.setMobile((String) value);
                    }
                    break;
                case "emergencyMobile":
                    if (value instanceof String) {
                        employee.setEmergencyMobile((String) value);
                    }
                    break;
                case "roles":
                    if (value instanceof String) {
                        employee.setRoles((String) value);
                    }
                    break;
                case "birthdate":
                case "dateOfJoining":
                    if (value instanceof String) {
                        // Convert the string to LocalDate
                        employee.setBirthdate(LocalDate.parse((String) value));
                    }
                    break;
                case "gender":
                    if (value instanceof String) {
                        employee.setGender((String) value);
                    }
                    break;
                case "bloodGroup":
                    if (value instanceof String) {
                        employee.setBloodGroup((String) value);
                    }
                    break;
                case "designation":
                    // Assuming value is the id of the designation
                    if (value instanceof String) {
                        Long designationId = Long.parseLong((String) value); // Convert string to Long
                        Designation designation = designationService.findById(designationId);
                        employee.setDesignation(designation);
                    }
                    break;
                case "team":
                    // Assuming value is the id of the team
                    if (value instanceof String) {
                        Long teamId = Long.parseLong((String) value); // Convert string to Long
                        Team team = teamService.findById(teamId);
                        employee.setTeam(team);
                    }
                    break;
                default:
                    System.out.println("Error Updating: No Case Found for " + key);
                    break;
            }
        });

        // Save the updated employee entity
        String response = employeeService.addUser(employee);
        if (response.equals("added")) {
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Employee Details Updated");
            apiResponseDTO.setData(null);
        } else {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Couldn't Update Employee Details");
            apiResponseDTO.setData(employee);
        }
        return ResponseEntity.ok(apiResponseDTO);
    }

    @DeleteMapping("/deleteEmployee")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> deleteEmployee(@RequestParam("id") Long employeeId) {
        try {
            if (employeeService.deleteEmployee(employeeId)) {
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Employee Deleted Successfully");
                apiResponseDTO.setData(null);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Couldn't Delete Employee");
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
}