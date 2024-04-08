package com.employeselfservice.controllers;

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
@RequestMapping("/auth/")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class DashboardController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private AttendanceService attendanceService;



    @GetMapping("/user/currentEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getEmployeeDetails(@RequestParam("id") String employeeId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Employee employee = employeeService.findEmployeeById(Long.parseLong(employeeId));
            if (employee != null) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Employee Record Fetched!");
                apiResponse.setData(employee);
                return ResponseEntity.ok(apiResponse);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Employee with ID " + employeeId + " not found");
                apiResponse.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }
        } catch (NumberFormatException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid employee ID format");
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/user/attendance")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> calculateAttendance(@RequestParam long id) {
        try {
            Attendance calculatedAttendance = attendanceService.calculateAttendance(id, LocalDate.now() );
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
            apiResponse.setMessage("Employee not found with ID: " + id);
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