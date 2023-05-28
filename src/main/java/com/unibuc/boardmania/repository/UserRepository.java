package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User SET trustScore = :trustScore WHERE id = :userId")
    void updateTrustScoreByUserId(Long userId, int trustScore);

}
