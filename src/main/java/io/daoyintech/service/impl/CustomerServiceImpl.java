package io.daoyintech.service.impl;

import io.daoyintech.domain.Customer;
import io.daoyintech.repository.CustomerRepository;
import io.daoyintech.service.CustomerService;
import io.daoyintech.web.dto.CustomerDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Inject
    CustomerRepository customerRepository;

    @Override
    public CustomerDto create(CustomerDto customerDto) {
        log.debug("Request to create Customer : {}", customerDto);
        var customer = new Customer(customerDto.getFirstName(), customerDto.getLastName(), customerDto.getEmail(), customerDto.getTelephone(), Collections.emptySet(), Boolean.TRUE);
        this.customerRepository.save(customer);
        return CustomerService.mapToDto(customer);
    }

    @Override
    public List<CustomerDto> findAll() {
        log.debug("Request to get all Customers");
        return this.customerRepository.findAll().stream().map(CustomerService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CustomerDto findById(Long id) {
        log.debug("Request to get Customer : {}", id);
        return this.customerRepository.findById(id).map(CustomerService::mapToDto).orElse(null);
    }

    @Override
    public List<CustomerDto> findAllActive() {
        log.debug("Request to get all active customers");
        return this.customerRepository.findAllByEnabled(true).stream().map(CustomerService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDto> findAllInactive() {
        log.debug("Request to get all inactive customers");
        return this.customerRepository.findAllByEnabled(false).stream().map(CustomerService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        var customer = this.customerRepository.findById(id).orElseThrow(() -> new IllegalStateException("Cannot find Customer with id " + id));
        customer.setEnabled(false);
        this.customerRepository.save(customer);
    }
}
