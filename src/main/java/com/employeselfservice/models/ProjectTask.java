package com.employeselfservice.models;

import jakarta.persistence.*;
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
@Entity
@Table(name = "project_task")
public class ProjectTask {

    public enum TaskStatus{
        TO_DO,IN_PROGRESS,DONE
    }

    public enum TaskPriority{
        NONE,LOW,MEDIUM,HIGH
    }

    public enum TaskType{
        TASK,BUG
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pt_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pm_id")
    private ProjectMember projectMember;

    @ManyToOne
    @JoinColumn(name = "p_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pt_created_by")
    private Employee createdByEmployee;

    @ManyToOne
    @JoinColumn(name = "pt_assigned_to")
    private Employee assignedToEmployee;

    @Column(name = "pt_description")
    private String description;

    @Column(name = "pt_created_on")
    private LocalDate createdOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "pt_status")
    private TaskStatus status;

    @Column(name = "pt_start_date")
    private LocalDate startDate;

    @Column(name = "pt_end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "pt_priority")
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "pt_type")
    private TaskType type;
}