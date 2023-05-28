package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.enums.UserEventPlace;
import com.unibuc.boardmania.enums.UserEventStatus;
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
    @Query("UPDATE user_event SET userEventPlace = :place WHERE user.id = :userId AND event.id = :eventId")
    void updateUserEventPlaceByUserIdAndEventId(Long userId, Long eventId, UserEventPlace place);

    @Modifying
    @Query("UPDATE user_event SET userEventStatus = :status WHERE user.id = :userId AND event.id = :eventId")
    void updateUserEventStatusByUserIdAndEventId(Long userId, Long eventId, UserEventStatus status);

    @Query("SELECT ue FROM user_event ue WHERE ue.event.id = :eventId and ue.deleted=false")
    List<UserEvent> getParticipantsByEventId(Long eventId);
}
