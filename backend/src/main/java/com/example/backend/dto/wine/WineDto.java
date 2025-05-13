package com.example.backend.dto.wine;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class WineDto {
    private Long id;
    private String name;
    private int year;
    private String type;
    private int price;
    private String producer;
    private String description;
    private String ownerDescription;
    private BigDecimal rate;
    private String agingMethod;
    private int sweetness;
    private String region;
    private String variety;
    private int percentage;
    private String imageUrl;
}
