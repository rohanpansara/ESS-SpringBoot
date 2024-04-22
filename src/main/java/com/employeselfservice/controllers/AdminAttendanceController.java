package com.employeselfservice.controllers;

import com.employeselfservice.dto.AttendanceDTO;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.services.AttendanceService;
import com.employeselfservice.services.EmployeeService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth/admin/attendance")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AdminAttendanceController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/getAttendanceData")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAttendanceData(@RequestParam("month") int askingMonth) {
        try {
            int currentYear = LocalDate.now().getYear();
            List<Employee> employeeList = employeeService.findAll();
            List<AttendanceDTO> attendanceDataList = new ArrayList<>();

            for (Employee employee : employeeList) {
                System.out.println("Inside");
                List<AttendanceDTO> employeeAttendanceData = attendanceService.getAttendanceForMonth(employee.getId(), askingMonth, currentYear);
                System.out.println("EmployeeAttendanceData");
                System.out.println(employeeAttendanceData);
                attendanceDataList.addAll(employeeAttendanceData);
            }

            apiResponse.setSuccess(true);
            apiResponse.setMessage("All Employee Attendance Data Fetched");
            apiResponse.setData(attendanceDataList);
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
