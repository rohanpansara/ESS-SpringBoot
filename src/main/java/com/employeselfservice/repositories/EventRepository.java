package com.employeselfservice.repositories;

import com.employeselfservice.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Long> {
}
