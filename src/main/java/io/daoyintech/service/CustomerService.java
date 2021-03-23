package io.daoyintech.service;

import io.daoyintech.domain.Customer;
import io.daoyintech.web.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    static CustomerDto mapToDto(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getTelephone());
    }

    CustomerDto create(CustomerDto customerDto);
    List<CustomerDto> findAll();
    CustomerDto findById(Long id);
    List<CustomerDto> findAllActive();
    List<CustomerDto> findAllInactive();
    void delete(Long id);
}
