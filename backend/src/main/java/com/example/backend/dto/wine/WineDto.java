package com.example.backend.dto.wine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WineDto {
    private Long id;
    private String name;
    private int year;
    private String type;
    private BigDecimal price;
    private String producer;
    private String description;
    private String ownerDescription;
    private BigDecimal rate;
    private String agingMethod;
    private String sweetness;
    private String region;
    private String variety;
    private BigDecimal percentage;
    private LocalDateTime dateAdded;
    private int volume;
    private String imageUrl;
}
