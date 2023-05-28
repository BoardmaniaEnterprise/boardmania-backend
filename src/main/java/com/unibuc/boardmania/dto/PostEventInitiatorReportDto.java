package com.unibuc.boardmania.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEventInitiatorReportDto {

    private Long eventId;

    private Long firstPlaceUserId;

    private Long secondPlaceUserId;

    private Long thirdPlaceUserId;

    private List<Long> absentUserIds;

}
