package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
@Entity
@Table(name = "project_log")
public class ProjectLog {

    public enum ActivityOn{
        Project,Task
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pl_id")
    private Long id;

    @Column(name = "pl_message")
    private String logMessage;

    @ManyToOne
    @JoinColumn(name = "pl_activity_by")
    @JsonBackReference(value = "employee")
    private Employee activityBy;

    @ManyToOne
    @JoinColumn(name = "p_id")
    @JsonBackReference(value = "project")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "pt_id")
    @JsonBackReference(value = "project_task")
    private ProjectTask task;

    @Enumerated(EnumType.STRING)
    @Column(name = "pl_activity_on")
    private ActivityOn activityOn;

}