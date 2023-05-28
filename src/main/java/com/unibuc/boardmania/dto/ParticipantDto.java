package com.unibuc.boardmania.dto;

import com.unibuc.boardmania.enums.UserEventPlace;
import com.unibuc.boardmania.enums.UserEventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {

    private Long id;
    private String firstName;
    private String lastName;
    private UserEventStatus userEventStatus;
    private UserEventPlace userEventPlace;
}
