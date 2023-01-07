package com.unibuc.boardmania.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameDto {
    private Long id;
    private String name;

    private int minNumberOfPlayers;

    private int maxNumberOfPlayers;

    private String description;

    private String url;
}
