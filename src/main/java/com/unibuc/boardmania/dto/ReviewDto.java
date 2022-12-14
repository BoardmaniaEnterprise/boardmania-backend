package com.unibuc.boardmania.dto;

import com.unibuc.boardmania.model.HonorTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private String comment;

    private HonorTitle honor;
}