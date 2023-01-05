package com.unibuc.boardmania.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameDto {
    private Long id;
    private String name;
}
