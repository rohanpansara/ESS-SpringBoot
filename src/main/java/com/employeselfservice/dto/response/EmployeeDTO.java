package com.employeselfservice.dto.response;

import com.employeselfservice.models.Designation;
import com.employeselfservice.models.Team;
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
public class EmployeeDTO {
    private Long id;
    private String name;
    private Designation designation;
    private Team team;
    private String role;
}
