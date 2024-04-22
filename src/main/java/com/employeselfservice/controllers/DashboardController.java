package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.EmployeeDAO;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.dto.response.DashboardDTO;
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
    private ApiResponse apiResponse;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private DashboardDTO dashboardDTO;

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
    public ResponseEntity<ApiResponse> getEmployeeDetails(@RequestHeader("Authorization") String authorizationHeader) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            if (employee != null) {
                EmployeeDAO employeeDAO = new EmployeeDAO();
                employeeDAO.setId(employee.getId());
                employeeDAO.setName(employee.getFirstname() + " " + employee.getLastname());
                employeeDAO.setTeam(employee.getTeam());
                employeeDAO.setDesignation(employee.getDesignation());
                employeeDAO.setRole(employee.getRoles());

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


    @GetMapping("/getDashboardData")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getDashboardData(@RequestHeader("Authorization") String authorizationHeader) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            String employeeEmail = jwtService.extractUsername(token);
            Employee employee = employeeService.findByEmail(employeeEmail);

            if (employee != null) {
                Attendance calculatedAttendance = attendanceService.calculateAttendance(employee.getId(), LocalDate.now());
                List<Leave> leaveList = leaveService.findAllLeavesForEmployee(employee.getId());

                DashboardDTO dashboardDTO = new DashboardDTO();
                dashboardDTO.setListOfHolidays(holidayService.findAllHolidays());
                dashboardDTO.setWorkHours(calculatedAttendance.getWorkHours());
                dashboardDTO.setFinalPunchOut(calculatedAttendance.getCanLeaveByTime());
                dashboardDTO.setNumberOfProjects(projectService.getNumberOfProjects());
                dashboardDTO.setListOfLeaves(leaveList);
                dashboardDTO.setNumberOfLeavesTaken((long) leaveList.size());
                dashboardDTO.setListOfEvents(eventService.getAllEvents());

                apiResponse.setSuccess(true);
                apiResponse.setMessage("Dashboard Data Fetched");
                apiResponse.setData(dashboardDTO);
                return ResponseEntity.ok(apiResponse);
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }
        } catch (NoSuchElementException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

}