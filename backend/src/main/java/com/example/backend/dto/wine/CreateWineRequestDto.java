package com.example.backend.dto.wine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String description;
    private String ownerDescription;
    @NotNull
    private BigDecimal rate;
    @NotBlank
    private String agingMethod;
    @Positive
    private int sweetness;
    @NotBlank
    private String region;
    @NotBlank
    private String variety;
    @Positive
    private int percentage;
    @NotBlank
    private String imageUrl;
}
