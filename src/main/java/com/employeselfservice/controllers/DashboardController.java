package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.EmployeeDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
import com.employeselfservice.dto.response.EmployeeDashboardDTO;
import com.employeselfservice.dto.response.WidgetsDTO;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Attendance;
import com.employeselfservice.models.Leave;
import com.employeselfservice.services.*;
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
    private ApiResponseDTO apiResponseDTO;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmployeeDTO employeeDTO;

    @Autowired
    private EmployeeDashboardDTO employeeDashboardDTO;

    @Autowired
    private WidgetsDTO widgetsDTO;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EventService eventService;



    @GetMapping("/currentEmployee")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> getEmployeeDetails(@RequestHeader("Authorization") String authorizationHeader) {
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO();
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            if (employee != null) {
                EmployeeDTO employeeDTO = new EmployeeDTO();
                employeeDTO.setId(employee.getId());
                employeeDTO.setName(employee.getFirstname() + " " + employee.getLastname());
                employeeDTO.setTeam(employee.getTeam());
                employeeDTO.setDesignation(employee.getDesignation());
                employeeDTO.setRole(employee.getRoles());

                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Employee Details Fetched!");
                apiResponseDTO.setData(employeeDTO);
                return ResponseEntity.ok(apiResponseDTO);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Employee not found");
                apiResponseDTO.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
            }
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }


    @GetMapping("/getDashboardData")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> getDashboardData(@RequestHeader("Authorization") String authorizationHeader) {
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO();
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            if (employee != null) {
                Attendance calculatedAttendance = attendanceService.calculateAttendance(employee.getId(), LocalDate.now());
                List<Leave> leaveList = leaveService.findAllLeavesForEmployee(employee.getId());

                employeeDashboardDTO.setListOfHolidays(holidayService.findAllHolidays());
                widgetsDTO.setWidgetPrimaryTwo(calculatedAttendance.getWorkHours());
                widgetsDTO.setWidgetSecondaryTwo(calculatedAttendance.getCanLeaveByTime());
                widgetsDTO.setWidgetPrimaryOne(String.valueOf(projectService.getProjectsAssignedToTheEmployee(employee.getId()).size()));
                widgetsDTO.setWidgetPrimaryThree(String.valueOf(leaveList.size()));
//                widgetsDTO.setWidgetSecondaryThree();
                employeeDashboardDTO.setListOfLeaves(leaveList);
                employeeDashboardDTO.setListOfEvents(eventService.getAllEvents());
                employeeDashboardDTO.setWidgetsDTO(widgetsDTO);

                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Dashboard Data Fetched");
                apiResponseDTO.setData(employeeDashboardDTO);
                return ResponseEntity.ok(apiResponseDTO);
            } else {
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
            }
        } catch (NoSuchElementException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

}