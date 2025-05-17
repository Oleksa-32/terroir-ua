package com.example.backend.dto.wine;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WineSearchParametersDto {
    private String name;
    private String[] types;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minYear;
    private Integer maxYear;
    private String[] producers;
}
