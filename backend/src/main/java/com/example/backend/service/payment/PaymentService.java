package com.example.backend.service.payment;

import com.example.backend.dto.payment.CreatePaymentRequestDto;
import com.example.backend.dto.payment.PaymentResponseDto;
import com.example.backend.model.Payment;
import com.stripe.exception.StripeException;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

public interface PaymentService {
    List<Payment> getPaymentsByUser(Long userId);

    PaymentResponseDto createSession(
            CreatePaymentRequestDto dto,
            UriComponentsBuilder uriBuilder
    ) throws StripeException;

    void handleSuccess(String sessionId) throws StripeException;

    void handleCancel(String sessionId);
}
