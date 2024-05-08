package com.employeselfservice.dto.response;

import com.employeselfservice.models.Event;
import com.employeselfservice.models.Holiday;
import com.employeselfservice.models.Leave;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDashboardDTO {
    private List<Holiday> listOfHolidays;
    private Long numberOfProjects;
    private String workHours;
    private String finalPunchOut;
    private Long numberOfLeavesTaken;
    private WidgetsDTO widgetsDTO;
    private List<Leave> listOfLeaves;
    private List<Event> listOfEvents;
}
