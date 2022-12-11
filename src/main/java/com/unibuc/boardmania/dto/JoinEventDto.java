package com.unibuc.boardmania.dto;

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
