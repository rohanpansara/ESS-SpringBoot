package com.employeselfservice.controllers;


import com.employeselfservice.dto.response.ApiResponse;
import com.employeselfservice.models.Attendance;
import com.employeselfservice.models.PunchIn;
import com.employeselfservice.models.PunchOut;
import com.employeselfservice.services.AttendanceService;
import com.employeselfservice.services.EmployeeService;
import com.employeselfservice.services.PunchInService;
import com.employeselfservice.services.PunchOutService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth/user/attendance")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PunchInService punchInService;

    @Autowired
    private PunchOutService punchOutService;

    @Autowired
    private ApiResponse apiResponse;

    @GetMapping("/getAllPR")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAttendanceForEmployee(@RequestParam long id) {
        try{
            List<Attendance> attendanceList = attendanceService.getAttendanceWhereEmployeeIsPR(id);
            if(attendanceList.isEmpty()){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("No Attendance Available Yet");
                apiResponse.setData(attendanceList);
            }
            else{
                apiResponse.setSuccess(true);
                apiResponse.setMessage("All Attendance Fetched");
                apiResponse.setData(attendanceList);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (NumberFormatException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid employee ID format");
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
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

    @GetMapping("/getAllAttendance")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> findAllByEmployeeId(@RequestParam long id) {
        try{
            List<Attendance> attendanceList = attendanceService.findAllByEmployeeId(id);
            if(attendanceList.isEmpty()){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("No Attendance Available Yet");
                apiResponse.setData(attendanceList);
            }
            else{
                apiResponse.setSuccess(true);
                apiResponse.setMessage("All Attendance Fetched");
                apiResponse.setData(attendanceList);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (NumberFormatException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid employee ID format");
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
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

    @GetMapping("/getAllPunches")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllPunches(@RequestParam long id, @RequestParam LocalDate date){
        try {

            List<PunchIn> allPunchIns = punchInService.getAllByEmployeeId(id, date);
            List<PunchOut> allPunchOuts = punchOutService.getAllByEmployeeId(id, date);

            Map<LocalDateTime, String> punchMap = attendanceService.mergePunchInsAndPunchOuts(allPunchIns,allPunchOuts);

            if(punchMap.isEmpty()){
                apiResponse.setSuccess(true);
                apiResponse.setMessage("No Punch Data Available");
                apiResponse.setData(punchMap);
            }
            else{
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Punches For Date-"+date);
                apiResponse.setData(punchMap);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (NumberFormatException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Invalid employee ID format");
            apiResponse.setData(null);
            return ResponseEntity.badRequest().body(apiResponse);
        } catch (NoSuchElementException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Employee not found with ID: " + id);
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        } catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Internal Error: "+e.getMessage());
            apiResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
