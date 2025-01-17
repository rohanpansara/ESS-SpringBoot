package com.employeselfservice.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardWidgetsDAO {
    private long numberOfEmployees;
    private long numberOfProjects;
    private long numberOfPendingLeaveRequests;
    private long numberOfTasks;
    private String averageWorkHours;
    private long numberOfApprovedLeaveRequests;
}
