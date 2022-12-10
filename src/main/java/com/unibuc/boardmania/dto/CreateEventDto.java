package com.unibuc.boardmania.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventDto {

    private Long initiatorId;

    private String name;

    private String description;

    private String location;

    private boolean online;

    private int maxNrOfPlayers;

    private Double minTrustScore;

}