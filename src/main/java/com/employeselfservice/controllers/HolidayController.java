package com.employeselfservice.controllers;

import com.employeselfservice.dto.response.ApiResponseDTO;
import com.employeselfservice.services.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLSyntaxErrorException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Requester-Type", exposedHeaders = "X-Get-Header")
public class HolidayController {

    @Autowired
    private ApiResponseDTO apiResponseDTO;

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/holiday/getAll")
    public ResponseEntity<ApiResponseDTO> getAllHolidays(){
        try{
            apiResponseDTO.setSuccess(true);
            apiResponseDTO.setMessage("Holidays Fetched");
            apiResponseDTO.setData(holidayService.findAllHolidays());
            return ResponseEntity.ok(apiResponseDTO);
        } catch (SQLSyntaxErrorException e) {
            apiResponseDTO.setSuccess(false);
            apiResponseDTO.setMessage("Database Error: "+e.getMessage());
            return ResponseEntity.badRequest().body(apiResponseDTO);
        }
    }
}
