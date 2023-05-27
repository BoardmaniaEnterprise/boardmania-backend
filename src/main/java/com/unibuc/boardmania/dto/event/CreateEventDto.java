package com.unibuc.boardmania.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventDto {

    private String name;

    private String description;

    private String location;

    private Long eventDateTimestamp;

    private Long votingDeadlineTimestamp;

    private Long confirmationDeadlineTimestamp;

    private boolean online;

    private int maxNrOfPlayers;

    private int minTrustScore;

    private List<Long> gameIds;

}
