package com.example.backend.controller;

import com.example.backend.dto.payment.CreatePaymentRequestDto;
import com.example.backend.dto.payment.PaymentResponseDto;
import com.example.backend.mapper.PaymentMapper;
import com.example.backend.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Payment management", description = "Endpoints for handling payments")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentsController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @GetMapping
    @Operation(summary = "Get user payments", description = "Retrieve all payments for a user"
            + " by their ID")
    public List<PaymentResponseDto> getPayments(@RequestParam("user_id") Long userId) {
        return paymentService.getPaymentsByUser(userId)
                .stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create payment session", description = "Create a new Stripe payment"
            + " session for a rental")
    public PaymentResponseDto createPayment(
            @RequestBody CreatePaymentRequestDto dto,
            UriComponentsBuilder uriBuilder
    ) throws StripeException {
        return paymentService.createSession(dto, uriBuilder);
    }

    @GetMapping("/success")
    @Operation(summary = "Handle payment success", description = "Endpoint Stripe redirects to"
            + " after successful payment")
    public String success(@RequestParam("session_id") String sessionId)
            throws StripeException {
        paymentService.handleSuccess(sessionId);
        return "Payment successful for session " + sessionId;
    }

    @GetMapping("/cancel")
    @Operation(summary = "Handle payment cancellation", description = "Endpoint Stripe redirects"
            + " to after payment cancellation")
    public String cancel(@RequestParam("session_id") String sessionId) {
        paymentService.handleCancel(sessionId);
        return "Payment cancelled; you have 24 hrs to retry using the same link.";
    }
}
