package com.employeselfservice.dao.request;

import com.employeselfservice.models.Designation;
import com.employeselfservice.models.Team;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEmployeeRequest {
    private List<Team> teams;
    private List<Designation> designations;
}
