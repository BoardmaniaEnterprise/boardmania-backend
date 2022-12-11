package com.unibuc.boardmania.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGameDto {

    private String name;

    private Integer minNumberOfPlayers;

    private Integer maxNumberOfPlayers;

    private String description;

    private String url;

    private Boolean deleted;
}
