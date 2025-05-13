package com.example.backend.dto.wine;

import lombok.Data;

@Data
public class WineItemDto {
    private String name;
    private int year;
    private int price;
}
