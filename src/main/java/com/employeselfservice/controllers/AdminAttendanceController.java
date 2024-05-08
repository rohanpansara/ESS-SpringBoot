package com.employeselfservice.controllers;

import com.employeselfservice.dto.response.AttendanceDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
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
    private ApiResponseDTO apiResponseDTO;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/getAttendanceData")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO> getAttendanceData(@RequestParam("month") int askingMonth) {
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

            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("All Employee Attendance Data Fetched");
            apiResponseDTO.setData(attendanceDataList);
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
