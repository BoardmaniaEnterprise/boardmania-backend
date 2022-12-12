package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    @Query("SELECT ue FROM UserEvent ue WHERE ue.event.id = :eventId AND ue.user.id = :userId AND ue.deleted=false")
    Optional<UserEvent> findByEventIdAndUserId(Long eventId, Long userId);
}
