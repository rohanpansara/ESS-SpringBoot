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
@Table(name = "project")
public class Project {

    public enum ProjectStatus {
        NEW,
        IN_PROGRESS,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Long id;

    @Column(name = "p_name", nullable = false)
    private String name;

    @Column(name = "p_key", nullable = false)
    private String key;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "p_owner_id")
    private Employee owner;

    @Column(name = "p_description")
    private String description;

    @Column(name = "p_created_on")
    private LocalDate createdOn;

    @Column(name = "p_initiation")
    private LocalDate initiation;

    @Column(name = "p_deadline")
    private LocalDate deadline;

    @Column(name = "p_completion")
    private LocalDate completion;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_status")
    private ProjectStatus status;

    @Column(name = "p_progress")
    private int progress;

    @Column(name = "p_last_activity")
    private LocalDate lastActivity;

    public Project(Long id){
        this.id = id;
    }
}