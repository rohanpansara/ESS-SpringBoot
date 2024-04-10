package com.employeselfservice.controllers;

import com.employeselfservice.Application;
import com.employeselfservice.dao.request.PunchRequest;
import com.employeselfservice.dao.response.ApiResponse;
import com.employeselfservice.models.Notifications;
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
    private PunchInService punchInService;

    @Autowired
    private PunchOutService punchOutService;

    @Autowired
    private NotificationService notificationService;

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

    @GetMapping("/notification")
    public ResponseEntity<ApiResponse> getAllNotificationsForEmployee(@RequestParam Long id) {
        try {
            List<Notifications> notifications = notificationService.getAllNotificationsForEmployee(id);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Notifications fetched!");
            apiResponse.setData(notifications);
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
