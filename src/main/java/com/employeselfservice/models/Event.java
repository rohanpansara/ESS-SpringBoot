package com.employeselfservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "event")
public class Event {

    public enum EventType {
        WORK,
        OFFICE_CULTURE,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ev_id")
    private Long id;

    @Column(name = "ev_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "ev_name", nullable = false)
    private String eventName;

    @Column(name = "ev_location", nullable = false)
    private String eventLocation;

    @Column(name = "ev_time", nullable = false)
    private LocalTime eventTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "ev_type", nullable = false, columnDefinition = "varchar(255) default 'OTHER'")
    private EventType eventType;

    @Column(name = "ev_createdBy")
    private Long createdById;
}
