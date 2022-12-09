package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
