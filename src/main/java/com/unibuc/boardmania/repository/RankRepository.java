package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
}
