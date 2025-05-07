package com.example.backend.dto.wine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateWineRequestDto {
    @NotBlank
    private String name;
    private int year;
    private String type;
    @Positive
    private int price;
    @NotBlank
    private String producer;
    private String description;
    private BigDecimal rate;
}
