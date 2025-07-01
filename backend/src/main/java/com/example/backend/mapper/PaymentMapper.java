package com.example.backend.mapper;

import com.example.backend.config.MapperConfig;
import com.example.backend.dto.payment.PaymentResponseDto;
import com.example.backend.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);
}
