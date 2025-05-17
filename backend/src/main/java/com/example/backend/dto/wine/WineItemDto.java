package com.example.backend.dto.wine;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class WineItemDto {
    private String name;
    private int year;
    private BigDecimal price;
}
