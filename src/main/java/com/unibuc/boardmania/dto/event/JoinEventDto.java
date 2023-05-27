package com.unibuc.boardmania.dto.event;

import com.unibuc.boardmania.dto.VoteDto;
import com.unibuc.boardmania.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinEventDto {

    List<VoteDto> voteDtoList;
}
