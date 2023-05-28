package com.unibuc.boardmania.service;

import com.unibuc.boardmania.dto.CreateReportDto;
import com.unibuc.boardmania.model.Event;
import com.unibuc.boardmania.model.Report;
import com.unibuc.boardmania.repository.EventRepository;
import com.unibuc.boardmania.repository.ReportRepository;
import com.unibuc.boardmania.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Long createReport(Long userId, CreateReportDto createReportDto) {
        if (userId.equals(createReportDto.getReportedUserId()))
            throw new BadRequestException("You cannot report yourself!");

        Event event = eventRepository.getById(createReportDto.getEventId());

        if (event.getEventDateTimestamp() > DateTime.now().getMillis())
            throw new BadRequestException("Event still hasn't taken place!");

        Report report = Report.builder()
                              .reason(createReportDto.getReason())
                              .reporter(userRepository.getById(userId))
                              .reportedUser(userRepository.getById(createReportDto.getReportedUserId()))
                              .event(event)
                              .build();
        return reportRepository.save(report).getId();
    }

}
