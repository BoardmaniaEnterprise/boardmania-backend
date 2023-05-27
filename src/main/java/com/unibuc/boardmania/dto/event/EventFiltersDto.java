package com.unibuc.boardmania.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFiltersDto {

    private String searchParam;

    //0 -> online / on site
    //1 -> only online
    //2 -> only on site
    private int locationType;

}
