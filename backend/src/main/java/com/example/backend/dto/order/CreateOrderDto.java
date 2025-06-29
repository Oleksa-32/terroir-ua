package com.example.backend.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String shippingAddress;
    @NotNull
    private int zipCode;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String details;
}
