package com.employeselfservice.controllers;

import com.employeselfservice.JWT.services.JWTService;
import com.employeselfservice.dto.response.BaseDTO;
import com.employeselfservice.dto.request.AddPunchRequestDTO;
import com.employeselfservice.dto.response.ApiResponseDTO;
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
    private ApiResponseDTO apiResponseDTO;

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
    private BaseDTO baseDTO;

    @PostMapping("/punch")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> handlePunch(@RequestBody AddPunchRequestDTO addPunchRequestDTO) {
        try {
            if (addPunchRequestDTO.getPunchType().equals("PunchIn")) {
                LocalTime currentTime = LocalTime.now();
                String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                String punchResponse = punchInService.addPunchIn(addPunchRequestDTO.getEmployeeId());
                if (punchResponse.equals("punched")) {
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("Punched In At "+formattedTime);
                    apiResponseDTO.setData(null);
                } else {
                    apiResponseDTO.setSuccess(false);
                    apiResponseDTO.setMessage("Error Punching In");
                    apiResponseDTO.setData(null);
                }
            } else if (addPunchRequestDTO.getPunchType().equals("PunchOut")) {
                LocalTime currentTime = LocalTime.now();
                String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                String punchResponse = punchOutService.addPunchOut(addPunchRequestDTO.getEmployeeId());
                if (punchResponse.equals("punched")) {
                    apiResponseDTO.setSuccess(true);
                    apiResponseDTO.setMessage("Punched Out At "+formattedTime);
                    apiResponseDTO.setData(null);
                } else {
                    apiResponseDTO.setSuccess(false);
                    apiResponseDTO.setMessage("Error Punching Out");
                    apiResponseDTO.setData(null);
                }
            } else {
                // invalid punch type
                apiResponseDTO.setSuccess(false);
                apiResponseDTO.setMessage("Invalid Punch Type");
                apiResponseDTO.setData(null);
                return ResponseEntity.badRequest().body(apiResponseDTO);
            }

            // on success response
            return ResponseEntity.ok(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/user/getNavbarData")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> getAllNotificationsForEmployee(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authorizationHeader);
            Employee employee = employeeService.findByEmail(jwtService.extractUsername(token));

            List<Notifications> notifications = notificationService.getAllNotificationsForEmployee(employee.getId());
            baseDTO.setEmployeeId(employee.getId());
            baseDTO.setFirstName(employee.getFirstname());
            baseDTO.setLastName(employee.getLastname());
            baseDTO.setNotificationsList(notifications);

            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Notifications fetched!");
            apiResponseDTO.setData(baseDTO);
            return ResponseEntity.ok().body(apiResponseDTO);
        } catch (NumberFormatException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Invalid employee ID format");
            apiResponseDTO.setData(null);
            return ResponseEntity.badRequest().body(apiResponseDTO);
        } catch (Exception e){
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: "+e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.badRequest().body(apiResponseDTO);
        }
    }
}
