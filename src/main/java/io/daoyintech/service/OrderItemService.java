package io.daoyintech.service;

import io.daoyintech.domain.OrderItem;
import io.daoyintech.web.dto.OrderItemDto;

import java.util.List;

public interface OrderItemService {

    static OrderItemDto mapToDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                orderItem.getOrder().getId()
        );
    }

    OrderItemDto findById(Long id);
    OrderItemDto create(OrderItemDto orderItemDto);
    void delete(Long id);
    List<OrderItemDto> findByOrderId(Long orderId);
}
