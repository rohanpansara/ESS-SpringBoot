package com.employeselfservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProjectActivityDTO {
    private String activity;
    private Long activityOn;
    private Long activityBy;
    private String changeFrom;
    private String changeTo;
}
