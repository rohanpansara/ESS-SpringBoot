package com.employeselfservice.dto;

import com.employeselfservice.models.Designation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDAO {
    private Long employeeId;
    private String name;
    private LocalDate dateOfJoining;
    private Designation designation;
    private int projectsAssigned;
    private int taskAssigned;
    private int taskDone;
}
