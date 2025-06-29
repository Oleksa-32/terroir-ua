package com.example.backend.dto.wine;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WineItemDto {
    private String name;
    private int year;
    private BigDecimal price;
}
