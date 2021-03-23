package io.daoyintech.service;

import io.daoyintech.domain.Cart;
import io.daoyintech.web.dto.CartDto;

import java.util.List;

public interface CartService {
    List<CartDto> findAll();
    List<CartDto> findAllActiveCarts();
    Cart create(Long customerId);
    CartDto createDto(Long customerId);
    CartDto findById(Long id);
    void delete(Long id);
    CartDto getActiveCart(Long customerId);

    static CartDto mapToDto(Cart cart) {
        return new CartDto(
                cart.getId(),
                CustomerService.mapToDto(cart.getCustomer()),
                cart.getStatus().name()
        );
    }
}
