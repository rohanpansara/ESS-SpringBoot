package com.employeselfservice.dto;

import com.employeselfservice.models.Leave;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDTO {
    private String employeeFullName;
    private LocalDate appliedOn;
    private String reason;
    private LocalDate from;
    private LocalDate to;
    private Leave.LeaveType type;
    private double overflow;
    private Leave.LeaveStatus status;
}

