package io.daoyintech.service;

import io.daoyintech.domain.Payment;
import io.daoyintech.web.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    static PaymentDto mapToDto(Payment payment, Long orderId) {
        if (payment != null) {
            return new PaymentDto(
                    payment.getId(),
                    payment.getPaypalPaymentId(),
                    payment.getStatus().name(),
                    orderId
            );
        } else {
            return null;
        }
    }

    List<PaymentDto> findByPriceRange(Double max);
    List<PaymentDto> findAll();
    PaymentDto findById(Long id);
    PaymentDto create(PaymentDto paymentDto);
    void delete(Long id);
}
