package com.example.backend.service.payment;

import com.example.backend.dto.payment.CreatePaymentRequestDto;
import com.example.backend.dto.payment.PaymentResponseDto;
import com.example.backend.dto.payment.PaymentStatus;
import com.example.backend.mapper.PaymentMapper;
import com.example.backend.model.Order;
import com.example.backend.model.Payment;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    @Override
    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByOrderUserId(userId);
    }

    @Override
    public PaymentResponseDto createSession(CreatePaymentRequestDto dto,
                                            UriComponentsBuilder uriBuilder)
            throws StripeException {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found: " + dto.getOrderId()));

        BigDecimal total = order.getTotalPrice();
        long amount = total
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        String successUrl = UriComponentsBuilder
                .fromHttpUrl(frontendBaseUrl)
                .path("/payments/success")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build().toUriString();
        String cancelUrl = UriComponentsBuilder
                .fromHttpUrl(frontendBaseUrl)
                .path("/payments/cancel")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build().toUriString();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("uah")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName("Order #" + order.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
        Session session = Session.create(params);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setAmount(amount);
        payment.setCurrency("uah");
        payment.setStatus(PaymentStatus.OPEN);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public void handleSuccess(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        if ("paid".equals(session.getPaymentStatus())) {
            Payment payment = paymentRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Payment not found for session: " + sessionId));
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
        }
    }

    @Override
    public void handleCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow();
        payment.setStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }
}
