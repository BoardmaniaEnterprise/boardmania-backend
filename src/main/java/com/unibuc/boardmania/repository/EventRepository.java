package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByDeletedFalse(Specification<Event> spec, Pageable pageable);

    List<Event> findAllByVotingDeadlineTimestampBeforeAndSentConfirmationEmailsFalseAndDeletedFalse(Long currentTimestamp);

    @Modifying
    @Query("UPDATE events SET sentConfirmationEmails = :value WHERE id = :eventId")
    void updateSentConfirmationEmails(Long eventId, boolean value);

    @Query("SELECT e FROM events e WHERE e.eventDateTimestamp >= :dayStartTimestamp " +
            "AND e.eventDateTimestamp <= :dayEndTimestamp")
    List<Event> findAllTakingPlaceTodayAndDeletedFalse(Long dayStartTimestamp, Long dayEndTimestamp);

    List<Event> findAllByInitiatorId(Long userId);

    @Query("SELECT e FROM events e WHERE e.id = :eventId")
    Event getById(Long eventId);

}
