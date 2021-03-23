package io.daoyintech.service.impl;

import io.daoyintech.domain.Order;
import io.daoyintech.domain.Payment;
import io.daoyintech.domain.enums.OrderStatus;
import io.daoyintech.domain.enums.PaymentStatus;
import io.daoyintech.repository.OrderRepository;
import io.daoyintech.repository.PaymentRepository;
import io.daoyintech.service.PaymentService;
import io.daoyintech.web.dto.PaymentDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    OrderRepository orderRepository;

    @Override
    public List<PaymentDto> findByPriceRange(Double max) {
        return this.paymentRepository.findAllByAmountBetween(BigDecimal.ZERO, BigDecimal.valueOf(max))
                .stream().map(payment -> PaymentService.mapToDto(payment, findOrderByPaymentId(payment.getId()).getId())).collect(Collectors.toList());
    }

    @Override
    public List<PaymentDto> findAll() {
        return this.paymentRepository.findAll().stream()
                .map(payment -> findById(payment.getId())).collect(Collectors.toList());
    }

    @Override
    public PaymentDto findById(Long id) {
        var order = findOrderByPaymentId(id);
        return this.paymentRepository.findById(id).map(payment -> PaymentService.mapToDto(payment, order.getId())).orElse(null);
    }

    @Override
    public PaymentDto create(PaymentDto paymentDto) {
        var order = this.orderRepository.findById(paymentDto.getOrderId())
                .orElseThrow(() -> new IllegalStateException("The Order does not exists!"));
        order.setStatus(OrderStatus.PAID);
        var payment = new Payment(paymentDto.getPaypalPaymentId(), PaymentStatus.valueOf(paymentDto.getStatus()), order.getPrice());
        this.paymentRepository.saveAndFlush(payment);
        this.orderRepository.saveAndFlush(order);
        return PaymentService.mapToDto(payment, order.getId());
    }

    private Order findOrderByPaymentId(Long paymentId) {
        return orderRepository.findByPaymentId(paymentId).orElseThrow(() -> new IllegalStateException("No order exists for the Payment ID " + paymentId));
    }

    @Override
    public void delete(Long id) {
        this.paymentRepository.deleteById(id);
    }
}
