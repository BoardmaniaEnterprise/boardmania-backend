package com.unibuc.boardmania.service;

import com.unibuc.boardmania.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    @Autowired
    private final ReportRepository reportRepository;
}
