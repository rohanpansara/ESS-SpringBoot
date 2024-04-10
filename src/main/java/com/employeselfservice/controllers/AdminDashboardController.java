package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dao.request.AdminDashboardWidgetsDAO;
import com.employeselfservice.dao.request.EmployeeDAO;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/admin")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AdminDashboardController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AdminDashboardWidgetsDAO adminDashboardWidgetsDao;

    @Autowired
    private EmployeeDAO employeeDAO;


    @GetMapping("/getCurrentAdminDetails")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getCurrentAdminDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {

            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee admin = employeeService.findByEmail(jwtService.extractUsername(token));

            employeeDAO.setId(admin.getId());
            employeeDAO.setName(admin.getFirstname()+" "+admin.getLastname());
            employeeDAO.setDesignation(admin.getDesignation());
            employeeDAO.setTeam(admin.getTeam());
            employeeDAO.setRole(admin.getRoles());

            apiResponse.setSuccess(true);
            apiResponse.setMessage("Admin Data Fetched!");
            apiResponse.setData(employeeDAO);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/getDashboardWidgetData")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getDashboardWidgetData(){
        try {

            adminDashboardWidgetsDao.setNumberOfProjects(projectService.getNumberOfProjects());
            adminDashboardWidgetsDao.setNumberOfEmployees(employeeService.getNumberOfEmployee());
            adminDashboardWidgetsDao.setNumberOfPendingLeaveRequests(leaveService.getNumberOfPendingLeaves());

            adminDashboardWidgetsDao.setNumberOfTasks(projectTaskService.getNumberOfTasks());
            adminDashboardWidgetsDao.setAverageWorkHours(attendanceService.calculateAverageWorkHours());
            adminDashboardWidgetsDao.setNumberOfApprovedLeaveRequests(leaveService.getNumberOfApprovedLeaves());

            apiResponse.setSuccess(true);
            apiResponse.setMessage("Admin Data Fetched!");
            apiResponse.setData(adminDashboardWidgetsDao);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

}
