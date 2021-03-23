package io.daoyintech.service.impl;

import io.daoyintech.domain.Order;
import io.daoyintech.domain.enums.OrderStatus;
import io.daoyintech.repository.CartRepository;
import io.daoyintech.repository.OrderRepository;
import io.daoyintech.repository.PaymentRepository;
import io.daoyintech.service.AddressService;
import io.daoyintech.service.OrderService;
import io.daoyintech.web.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class OrderServiceImpl implements OrderService {
    @Inject
    OrderRepository orderRepository;

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    CartRepository cartRepository;

    @Override
    public List<OrderDto> findALl() {
        log.debug("Request to get all orders");
        return this.orderRepository.findAll().stream().map(OrderService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto findById(Long id) {
        log.debug("Request to get order : {}", id);
        return this.orderRepository.findById(id).map(OrderService::mapToDto).orElse(null);
    }

    @Override
    public List<OrderDto> findAllByUser(Long customerId) {
        log.debug("Request to get order by customer : {}", customerId);
        return this.orderRepository.findByCartCustomerId(customerId).stream().map(OrderService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto create(OrderDto orderDto) {
        log.debug("Request to create order : {}", orderDto);
        var cartId = orderDto.getCart().getId();
        var cart =  this.cartRepository.findById(cartId).orElseThrow(() -> new IllegalStateException("The Cart with ID[" + cartId + "] was not found !"));
        var order = new Order(
                BigDecimal.ZERO,
                OrderStatus.CREATION,
                null,
                null,
                AddressService.createFromDto(orderDto.getShipmentAddress()),
                Collections.emptySet(),
                cart
        );
        this.orderRepository.save(order);
        return OrderService.mapToDto(order);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        var order = this.orderRepository.findById(id).orElseThrow(() -> new IllegalStateException("Order with ID[" + id + "] cannot be found!"));
        Optional.ofNullable(order.getPayment()).ifPresent(paymentRepository::delete);
        orderRepository.delete(order);
    }

    @Override
    public boolean existsById(Long id) {
        return this.orderRepository.existsById(id);
    }
}
