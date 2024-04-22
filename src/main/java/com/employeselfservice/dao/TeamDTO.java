package com.employeselfservice.dto;

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
public class TeamDTO {
    private Long teamId;
    private String teamName;
    private String managerName;
    private String techLeadName;
    private int teamMembers;
}
