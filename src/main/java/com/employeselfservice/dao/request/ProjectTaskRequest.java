package com.employeselfservice.dao.request;

import com.employeselfservice.models.ProjectTask;
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

    private String taskDescription;
    private Long assignedToId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectTask.TaskStatus status;
    private ProjectTask.TaskType type;
    private ProjectTask.TaskPriority priority;

    @Override
    public String toString() {
        return "ProjectTaskRequest{" +
                "taskDescription='" + taskDescription + '\'' +
                ", assignedToId=" + assignedToId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", type=" + type +
                ", priority=" + priority +
                '}';
    }
}
