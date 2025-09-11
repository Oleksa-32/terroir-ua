package com.example.backend.dto.wine;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WineRecommendationDto extends WineItemDto {
    private Long id;
    private String imageUrl;
}
