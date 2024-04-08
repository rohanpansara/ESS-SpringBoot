package com.employeselfservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "project_member")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "p_id")
//    @JsonBackReference(value = "project")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "e_id")
//    @JsonBackReference(value = "employee")
    private Employee employee;

    @Column(name = "pm_start_date")
    private LocalDate joiningDate;

    @Column(name = "pm_end_date")
    private LocalDate leavingDate;

    public ProjectMember(Long assignedToId) {
        this.employee = new Employee(assignedToId);
    }
}