package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.services.DesignationService;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

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
    private JWTService jwtService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/addNewEmployee")
    public ResponseEntity<ApiResponse> addEmployee(@RequestBody Employee employee) {
        ApiResponse apiResponse = new ApiResponse();
        System.out.println(employee);
        try {
            String firstName = employee.getFirstname();
            String email = employee.getEmail();
            String password = employee.getPassword();
            String response = employeeService.addUser(employee);
            if(response.equals("added")){
                System.out.println("About to send email");
                emailService.sendWelcomeEmail(firstName,email,password);
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
}
