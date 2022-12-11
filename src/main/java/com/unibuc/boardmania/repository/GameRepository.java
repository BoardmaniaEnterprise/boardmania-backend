package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.dto.UpdateGameDto;
import com.unibuc.boardmania.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findAllByDeletedFalse();

    Optional<Game> findByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Game g SET g.deleted = true WHERE g.id = :id")
    void deleteGame(Long id);

}
