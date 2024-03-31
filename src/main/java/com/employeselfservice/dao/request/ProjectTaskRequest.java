package com.employeselfservice.dao.request;

import com.employeselfservice.models.ProjectTask.TaskPriority;
import com.employeselfservice.models.ProjectTask.TaskStatus;
import com.employeselfservice.models.ProjectTask.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectTaskRequest {

    private Long projectMemberId;
    private Long projectId;
    private Long createdByEmployeeId;
    private Long assignedToEmployeeId;
    private String taskDescription;
    private LocalDate taskCreatedOn;
    private TaskStatus taskStatus;
    private LocalDate taskStartDate;
    private LocalDate taskEndDate;
    private TaskPriority taskPriority;
    private TaskType taskType;
}
