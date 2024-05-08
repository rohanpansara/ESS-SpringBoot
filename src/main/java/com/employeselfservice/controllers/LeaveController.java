package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.request.AddLeaveRequestDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
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
@RequestMapping("/auth/user/leaves")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class LeaveController {

    @Autowired
    private ApiResponseDTO apiResponseDTO;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/getAllLeave")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> getAllLeavesForUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            if(employee==null){
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Employee ID not found");
                apiResponseDTO.setData(null);
            }
            List<Leave> leaves = leaveService.findAllLeavesForEmployee(employee.getId());
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Leave Applications Fetched");
            apiResponseDTO.setData(leaves);
            return ResponseEntity.ok(apiResponseDTO);
        } catch (EmptyResultDataAccessException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("No Leave Applications Found From You");
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
        }catch (AccessDeniedException e) {
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

    @PostMapping("/applyLeave")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> applyForLeave(@RequestBody AddLeaveRequestDTO addLeaveRequestDTO){
        try {
            LocalDate today = LocalDate.now();
            LocalDate leaveFrom = addLeaveRequestDTO.getLeaveFrom();
            LocalDate leaveTo = addLeaveRequestDTO.getLeaveTo();

            if (leaveFrom.isAfter(today.minusDays(1))) {
                if (leaveTo.isBefore(leaveFrom)) {
                    apiResponseDTO.setSuccess(false);
                    apiResponseDTO.setMessage("'Absence To' cannot be before 'Absence From'");
                    apiResponseDTO.setData(null);
                    return ResponseEntity.badRequest().body(apiResponseDTO);
                } else {
                    String leaveResponse = leaveService.applyForLeave(addLeaveRequestDTO);
                    if (leaveResponse.equals("Leave_Applied")) {
                        apiResponseDTO.setSuccess(true);
                        apiResponseDTO.setMessage("Leave Application Sent");
                        apiResponseDTO.setData(null);
                    }
                    else if(leaveResponse.equals("User_Not_Found")){
                        apiResponseDTO.setSuccess(false);
                        apiResponseDTO.setMessage("User Not Found");
                        apiResponseDTO.setData(null);
                    }
                    else {
                        apiResponseDTO.setSuccess(false);
                        apiResponseDTO.setMessage("Error Applying For Leave");
                        apiResponseDTO.setData(null);
                        return ResponseEntity.badRequest().body(apiResponseDTO);
                    }
                }
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Cannot Apply For Leave Before " + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                apiResponseDTO.setData(null);
                return ResponseEntity.badRequest().body(apiResponseDTO);
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
