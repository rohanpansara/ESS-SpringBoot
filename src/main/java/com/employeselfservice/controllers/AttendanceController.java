package com.employeselfservice.controllers;


import com.employeselfservice.dto.response.ApiResponseDTO;
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
    private ApiResponseDTO apiResponseDTO;

    @GetMapping("/getAllPR")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> getAttendanceForEmployee(@RequestParam long id) {
        try{
            List<Attendance> attendanceList = attendanceService.getAttendanceWhereEmployeeIsPR(id);
            if(attendanceList.isEmpty()){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("No Attendance Available Yet");
                apiResponseDTO.setData(attendanceList);
            }
            else{
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("All Attendance Fetched");
                apiResponseDTO.setData(attendanceList);
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (NumberFormatException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Invalid employee ID format");
            apiResponseDTO.setData(null);
            return ResponseEntity.badRequest().body(apiResponseDTO);
        } catch (NoSuchElementException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Employee not found with ID: " + id);
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/getAllAttendance")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> findAllByEmployeeId(@RequestParam long id) {
        try{
            List<Attendance> attendanceList = attendanceService.findAllByEmployeeId(id);
            if(attendanceList.isEmpty()){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("No Attendance Available Yet");
                apiResponseDTO.setData(attendanceList);
            }
            else{
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("All Attendance Fetched");
                apiResponseDTO.setData(attendanceList);
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (NumberFormatException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Invalid employee ID format");
            apiResponseDTO.setData(null);
            return ResponseEntity.badRequest().body(apiResponseDTO);
        } catch (NoSuchElementException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Employee not found with ID: " + id);
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
        } catch (Exception e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: " + e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }

    @GetMapping("/getAllPunches")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponseDTO> getAllPunches(@RequestParam long id, @RequestParam LocalDate date){
        try {

            List<PunchIn> allPunchIns = punchInService.getAllByEmployeeId(id, date);
            List<PunchOut> allPunchOuts = punchOutService.getAllByEmployeeId(id, date);

            Map<LocalDateTime, String> punchMap = attendanceService.mergePunchInsAndPunchOuts(allPunchIns,allPunchOuts);

            if(punchMap.isEmpty()){
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("No Punch Data Available");
                apiResponseDTO.setData(punchMap);
            }
            else{
                apiResponseDTO.setSuccess(true);
                apiResponseDTO.setMessage("Punches For Date-"+date);
                apiResponseDTO.setData(punchMap);
            }
            return ResponseEntity.ok(apiResponseDTO);
        } catch (NumberFormatException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Invalid employee ID format");
            apiResponseDTO.setData(null);
            return ResponseEntity.badRequest().body(apiResponseDTO);
        } catch (NoSuchElementException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Employee not found with ID: " + id);
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
        } catch (Exception e){
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Internal Error: "+e.getMessage());
            apiResponseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
        }
    }
}
