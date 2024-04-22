package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.BaseDAO;
import com.employeselfservice.dto.request.PunchRequest;
import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Notifications;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.NotificationService;
import com.employeselfservice.services.PunchInService;
import com.employeselfservice.services.PunchOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class BaseController {

    @Autowired
    private ApiResponse apiResponse;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PunchInService punchInService;

    @Autowired
    private PunchOutService punchOutService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BaseDAO baseDAO;

    @PostMapping("/punch")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> handlePunch(@RequestBody PunchRequest punchRequest) {
        try {
            if (punchRequest.getPunchType().equals("PunchIn")) {
                LocalTime currentTime = LocalTime.now();
                String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                String punchResponse = punchInService.addPunchIn(punchRequest.getEmployeeId());
                if (punchResponse.equals("punched")) {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Punched In At "+formattedTime);
                    apiResponse.setData(null);
                } else {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("Error Punching In");
                    apiResponse.setData(null);
                }
            } else if (punchRequest.getPunchType().equals("PunchOut")) {
                LocalTime currentTime = LocalTime.now();
                String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                String punchResponse = punchOutService.addPunchOut(punchRequest.getEmployeeId());
                if (punchResponse.equals("punched")) {
                    apiResponse.setSuccess(true);
                    apiResponse.setMessage("Punched Out At "+formattedTime);
                    apiResponse.setData(null);
                } else {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("Error Punching Out");
                    apiResponse.setData(null);
                }
            } else {
                // invalid punch type
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Invalid Punch Type");
                apiResponse.setData(null);
                return ResponseEntity.badRequest().body(apiResponse);
            }

            // on success response
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: " + e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }

    @GetMapping("/user/getNavbarData")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllNotificationsForEmployee(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Notifications> notifications = notificationService.getAllNotificationsForEmployee(employee.getId());
            baseDAO.setEmployeeId(employee.getId());
            baseDAO.setFirstName(employee.getFirstname());
            baseDAO.setLastName(employee.getLastname());
            baseDAO.setNotificationsList(notifications);

            apiResponse.setSuccess(true);
            apiResponse.setMessage("Notifications fetched!");
            apiResponse.setData(baseDAO);
            return ResponseEntity.ok().body(apiResponse);
        } catch (NumberFormatException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid employee ID format");
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
        } catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: "+e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
