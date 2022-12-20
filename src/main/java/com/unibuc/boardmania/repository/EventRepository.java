package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByDeletedFalse();
}
