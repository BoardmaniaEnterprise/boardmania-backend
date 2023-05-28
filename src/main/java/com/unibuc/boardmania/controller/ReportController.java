package com.unibuc.boardmania.controller;

import com.unibuc.boardmania.dto.CreateReportDto;
import com.unibuc.boardmania.service.ReportService;
import com.unibuc.boardmania.utils.KeycloakHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ReportController {

    @Autowired
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> createReport(Authentication authentication,
                                       @RequestBody CreateReportDto createReportDto) {
        return new ResponseEntity<>(reportService.createReport(KeycloakHelper.getUserId(authentication), createReportDto), HttpStatus.CREATED);
    }

}
