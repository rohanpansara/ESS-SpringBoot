package com.employeselfservice.models;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pl_activity_by")
    private Employee activityBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "p_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pt_id")
    private ProjectTask task;

    @Enumerated(EnumType.STRING)
    @Column(name = "pl_activity_on")
    private ActivityOn activityOn;

}
