package com.employeselfservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttendanceDTO {
    private Long employeeId;
    private String employeeName;
    private String averageWorkHours;
    private String totalWorkHours;
    private int earlyIns;
    private int lateOuts;
    private String teamName;
    private int leavesApproved;
    private int leavesApplied;
}
