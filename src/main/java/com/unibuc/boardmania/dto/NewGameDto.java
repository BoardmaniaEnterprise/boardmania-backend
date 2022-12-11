package com.unibuc.boardmania.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewGameDto {

    private String name;

    private int minNumberOfPlayers;

    private int maxNumberOfPlayers;

    private String description;

    private String url;

}
