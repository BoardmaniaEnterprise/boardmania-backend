package com.unibuc.boardmania.repository;

import com.unibuc.boardmania.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
