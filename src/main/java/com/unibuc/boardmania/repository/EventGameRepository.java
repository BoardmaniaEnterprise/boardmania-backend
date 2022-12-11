package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.EventGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventGameRepository extends JpaRepository<EventGame, Long> {

    @Query("SELECT ev FROM EventGame ev WHERE ev.event.id = :eventId AND ev.game.id = :gameId AND ev.deleted=false")
    Optional<EventGame> findByEventIdAndGameId(Long eventId, Long gameId);
}
