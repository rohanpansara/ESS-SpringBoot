package com.employeselfservice.dto.request;

import com.employeselfservice.models.Designation;
import com.employeselfservice.models.Team;
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
public class AddEmployeeRequestDTO {
    private List<Team> teams;
    private List<Designation> designations;
}
