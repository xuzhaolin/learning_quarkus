package io.daoyintech.service.impl;

import io.daoyintech.domain.OrderItem;
import io.daoyintech.repository.OrderItemRepository;
import io.daoyintech.repository.OrderRepository;
import io.daoyintech.repository.ProductRepository;
import io.daoyintech.service.OrderItemService;
import io.daoyintech.web.dto.OrderItemDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ProductRepository productRepository;

    @Override
    public OrderItemDto findById(Long id) {
        log.debug("Request to get OrderItem : {}", id);
        return this.orderItemRepository.findById(id).map(OrderItemService::mapToDto).orElse(null);
    }

    @Override
    public OrderItemDto create(OrderItemDto orderItemDto) {
        log.debug("Request to create OrderItem : {}", orderItemDto);
        var order = this.orderRepository.findById(orderItemDto.getOrderId()).orElseThrow(() -> new IllegalStateException("The Order does not exits!"));
        var product = this.productRepository.findById(orderItemDto.getProductId()).orElseThrow(() -> new IllegalStateException("The Product does not exist!"));
        var orderItem = this.orderItemRepository.save(new OrderItem(orderItemDto.getQuantity(), product, order));
        this.orderItemRepository.save(orderItem);
        return OrderItemService.mapToDto(orderItem);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OrderItem : {}", id);
        var orderItem = this.orderItemRepository.findById(id).orElseThrow(() -> new IllegalStateException("The orderItem does not exist"));
        var order = orderItem.getOrder();
        order.setPrice(order.getPrice().subtract(orderItem.getProduct().getPrice()));
        order.getOrderItems().remove(orderItem);
        this.orderRepository.save(order);
    }

    @Override
    public List<OrderItemDto> findByOrderId(Long orderId) {
        log.debug("Request to get all OrderItems of OrderId {}", orderId);
        return this.orderItemRepository.findAllByOrderId(orderId).stream().map(OrderItemService::mapToDto).collect(Collectors.toList());
    }
}
