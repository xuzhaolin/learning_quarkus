package io.daoyintech.service.impl;

import io.daoyintech.domain.Cart;
import io.daoyintech.domain.enums.CartStatus;
import io.daoyintech.repository.CartRepository;
import io.daoyintech.repository.CustomerRepository;
import io.daoyintech.service.CartService;
import io.daoyintech.web.dto.CartDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class CarServiceImpl implements CartService  {

    @Inject
    CartRepository cartRepository;

    @Inject
    CustomerRepository customerRepository;

    @Override
    public List<CartDto> findAll() {
        log.debug("Request to get all Carts");
        return this.cartRepository.findAll().stream().map(CartService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CartDto> findAllActiveCarts() {
        return this.cartRepository.findByStatus(CartStatus.NEW).stream().map(CartService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Cart create(Long customerId) {
        if (this.getActiveCart(customerId) == null) {
            var customer = this.customerRepository.findById(customerId).orElseThrow(() -> new IllegalStateException("The customer does not exist"));
            var cart = new Cart(customer, CartStatus.NEW);
            return this.cartRepository.save(cart);
        } else {
            throw new IllegalStateException("There is already an active cart");
        }
    }

    @Override
    public CartDto createDto(Long customerId) {
        return CartService.mapToDto(this.create(customerId));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public CartDto findById(Long id) {
        log.debug("Request to get Cart: {}", id);
        return this.cartRepository.findById(id).map(CartService::mapToDto).orElse(null);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Cart: {}", id);
        var cart = this.cartRepository.findById(id).orElseThrow(() ->  new IllegalStateException("Cannot find cart with id " + id));
        cart.setStatus(CartStatus.CANCELED);
        this.cartRepository.save(cart);
    }

    @Override
    public CartDto getActiveCart(Long customerId) {
        var carts = this.cartRepository.findByStatusAndCustomerId(CartStatus.NEW, customerId);
        if (carts != null) {
            if (carts.size() == 1) {
                return CartService.mapToDto(carts.get(0));
            } else {
                throw new IllegalStateException("Many active carts detected !!!");
            }
        }
        return null;
    }

}
