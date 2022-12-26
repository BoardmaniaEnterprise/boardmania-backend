package com.unibuc.boardmania.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;

    private String name;

    private String description;

    private String location;

    private Long eventDateTimestamp;

    private Long votingDeadlineTimestamp;

    private Long confirmationDeadlineTimestamp;

    private int minTrustScore;

    private int maxNumberOfPlayers;

    private boolean online;

}
