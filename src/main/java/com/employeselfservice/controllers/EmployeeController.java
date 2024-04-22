package com.employeselfservice.controllers;

import com.employeselfservice.JWT.dao.AuthRequest;
import com.employeselfservice.JWT.dao.AuthResponse;
import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.services.DesignationService;
import com.employeselfservice.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class EmployeeController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DesignationService designationService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public AuthResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            System.out.println("Authenticated");
            String token = jwtService.generateToken(authRequest.getUsername());
            Employee employee = employeeService.findByEmail(authRequest.getUsername());
            System.out.println(employee);
            return new AuthResponse(token, employee);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

}

