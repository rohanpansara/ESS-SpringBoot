package com.employeselfservice.dao;

import com.employeselfservice.models.Designation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDAO {
    private String name;
    private Designation designation;
    private int projectsAssigned;
    private int taskAssigned;
    private int taskDone;
}
