package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    @Query("SELECT ue FROM user_event ue WHERE ue.event.id = :eventId AND ue.user.id = :userId AND ue.deleted=false")
    Optional<UserEvent> findByEventIdAndUserId(Long eventId, Long userId);

    @Modifying
    @Query("UPDATE user_event SET sentConfirmationEmail = :value WHERE id = :userEventId")
    void updateSentConfirmationEmail(Long userEventId, boolean value);

    @Modifying
    @Query("UPDATE user_event SET confirmed = :value WHERE user.id = :userId AND event.id = :eventId")
    void updateConfirmedByUserIdAndEventId(Long userId, Long eventId, boolean value);

    @Query("SELECT ue FROM user_event ue WHERE ue.event.id = :eventId")
    List<UserEvent> getParticipantsByEventId(Long eventId);
}
