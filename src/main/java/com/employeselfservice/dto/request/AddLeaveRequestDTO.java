package com.employeselfservice.dto.request;
import com.employeselfservice.models.Leave;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddLeaveRequestDTO {

    private Long employeeId;
    private String leaveReason;
    private LocalDate leaveFrom;
    private LocalDate leaveTo;
    private LocalDate leaveAppliedOn;
    private Leave.LeaveStatus leaveStatus;
    private Leave.LeaveType leaveType;
    private String leaveCount;
}
