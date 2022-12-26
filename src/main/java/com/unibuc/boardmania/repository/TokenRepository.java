package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository  extends JpaRepository<Token, Long> {

    Optional<Token> findByValue(UUID value);

    @Modifying
    void deleteByValue(UUID value);

    @Query("SELECT t FROM tokens t WHERE t.user.id = :userId AND t.relatedObjectId = :eventId")
    Token findByUserIdAndEventId(Long userId, Long eventId);

}
