package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.request.LeaveRequest;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Leave;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.LeaveService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class LeaveController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/user/leaves/getAllLeave")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllLeavesForUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            if(employee==null){
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Employee ID not found");
                apiResponse.setData(null);
            }
            List<Leave> leaves = leaveService.findAllLeavesForEmployee(employee.getId());
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Leave Applications Fetched");
            apiResponse.setData(leaves);
            return ResponseEntity.ok(apiResponse);
        } catch (EmptyResultDataAccessException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("No Leave Applications Found From You");
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }catch (AccessDeniedException e) {
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

    @PostMapping("/user/leaves/applyLeave")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> applyForLeave(@RequestBody LeaveRequest leaveRequest){
        try {
            LocalDate today = LocalDate.now();
            LocalDate leaveFrom = leaveRequest.getLeaveFrom();
            LocalDate leaveTo = leaveRequest.getLeaveTo();

            if (leaveFrom.isAfter(today.minusDays(1))) {
                if (leaveTo.isBefore(leaveFrom)) {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("'Absence To' cannot be before 'Absence From'");
                    apiResponse.setData(null);
                    return ResponseEntity.badRequest().body(apiResponse);
                } else {
                    String leaveResponse = leaveService.applyForLeave(leaveRequest);
                    if (leaveResponse.equals("Leave_Applied")) {
                        apiResponse.setSuccess(true);
                        apiResponse.setMessage("Leave Application Sent");
                        apiResponse.setData(null);
                    }
                    else if(leaveResponse.equals("User_Not_Found")){
                        apiResponse.setSuccess(false);
                        apiResponse.setMessage("User Not Found");
                        apiResponse.setData(null);
                    }
                    else {
                        apiResponse.setSuccess(false);
                        apiResponse.setMessage("Error Applying For Leave");
                        apiResponse.setData(null);
                        return ResponseEntity.badRequest().body(apiResponse);
                    }
                }
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Cannot Apply For Leave Before " + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                apiResponse.setData(null);
                return ResponseEntity.badRequest().body(apiResponse);
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
