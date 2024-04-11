package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.EmployeeDAO;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Attendance;
import com.employeselfservice.services.AttendanceService;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth/user")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class DashboardController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private AttendanceService attendanceService;



    @GetMapping("/currentEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getEmployeeDetails(@RequestHeader("Authorization") String authorizationHeader) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            employeeDAO.setId(employee.getId());
            employeeDAO.setName(employee.getFirstname()+" "+employee.getLastname());
            employeeDAO.setTeam(employee.getTeam());
            employeeDAO.setDesignation(employee.getDesignation());
            employeeDAO.setRole(employee.getRoles());

            if (employee != null) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Employee Details Fetched!");
                apiResponse.setData(employeeDAO);
                return ResponseEntity.ok(apiResponse);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Employee not found");
                apiResponse.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> calculateAttendance(@RequestHeader("Authorization") String authorizationHeader) {
        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            Attendance calculatedAttendance = attendanceService.calculateAttendance(employee.getId(), LocalDate.now());
            if (calculatedAttendance != null) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Attendance Fetched");
                apiResponse.setData(calculatedAttendance);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Couldn't Fetch Attendance");
                apiResponse.setData(null);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (NoSuchElementException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Employee not found");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}