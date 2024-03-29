package com.unibuc.boardmania.dto.event;

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

    private String initiatorName;

    private String name;

    private String description;

    private String location;

    private Long eventDateTimestamp;

    private Long votingDeadlineTimestamp;

    private Long confirmationDeadlineTimestamp;

    private boolean joined;

    private int minTrustScore;

    private int maxNumberOfPlayers;

    private boolean online;

}
