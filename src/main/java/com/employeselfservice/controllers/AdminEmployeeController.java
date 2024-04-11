package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.request.AddEmployeeRequest;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
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

import java.util.List;

@RestController
@RequestMapping("/auth/admin/employee/")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AdminEmployeeController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DesignationService designationService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private AddEmployeeRequest addEmployeeRequest;

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
    public ResponseEntity<ApiResponse> getAllEmployees() {
        try {
            List<Employee> employees = employeeService.findAll();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("All Employee Records Fetched!");
            apiResponse.setData(employees);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/getTeamsAndDesignations")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getTeamAndDesignations() {
        try {
            addEmployeeRequest.setDesignations(designationService.getAllDesignations());
            addEmployeeRequest.setTeams(teamService.getAllTeams());

            if (addEmployeeRequest.getDesignations().isEmpty() && addEmployeeRequest.getTeams().isEmpty()) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No Teams/Designations Added By Admin Yet");
                apiResponse.setData(null);
            } else {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("All Teams/Designations Fetched");
                apiResponse.setData(addEmployeeRequest);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @PostMapping("/addNewEmployee")
    public ResponseEntity<ApiResponse> addEmployee(@RequestBody Employee employee) {
        try {
            String firstName = employee.getFirstname();
            String email = employee.getEmail();
            String password = employee.getPassword();
            String response = employeeService.addUser(employee);
            if (response.equals("added")) {
                System.out.println("About to send email");
                emailService.sendWelcomeEmail(firstName, email, password);
            }
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Employee added successfully");
            apiResponse.setData(employee);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @DeleteMapping("/deleteEmployee")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteEmployee(@RequestParam Long employeeId) {
        try {
            if (employeeService.deleteEmployee(employeeId)) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Employee Deleted Successfully");
                apiResponse.setData(null);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Delete Employee");
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
}