package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.Event;
import com.unibuc.boardmania.model.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByDeletedFalse();

    List<Event> findAllByVotingDeadlineTimestampBeforeAndSentConfirmationEmailsFalseAndDeletedFalse(Long currentTimestamp);

    @Modifying
    @Query("UPDATE events SET sentConfirmationEmails = :value WHERE id = :eventId")
    void updateSentConfirmationEmails(Long eventId, boolean value);

}
