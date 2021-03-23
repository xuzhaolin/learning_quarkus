package io.daoyintech.service;

import io.daoyintech.domain.Order;
import io.daoyintech.web.dto.OrderDto;
import io.daoyintech.web.dto.OrderItemDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface OrderService {

    static OrderDto mapToDto(Order order) {
        Set<OrderItemDto> orderItems = order.getOrderItems().stream().map(OrderItemService::mapToDto).collect(Collectors.toSet());
        return new OrderDto(
                order.getId(),
                order.getPrice(),
                order.getStatus().name(),
                order.getShipped(),
                order.getPayment() != null ? order.getPayment().getId() : null,
                AddressService.mapToDto(order.getShipmentAddress()),
                orderItems,
                CartService.mapToDto(order.getCart())
        );
    }

    List<OrderDto> findALl();
    OrderDto findById(Long id);
    List<OrderDto> findAllByUser(Long id);
    OrderDto create(OrderDto orderDto);
    void delete(Long id);
    boolean existsById(Long id);
}
