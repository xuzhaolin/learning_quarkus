package io.daoyintech.service.impl;

import io.daoyintech.domain.Product;
import io.daoyintech.domain.enums.ProductStatus;
import io.daoyintech.repository.CategoryRepository;
import io.daoyintech.repository.ProductRepository;
import io.daoyintech.service.ProductService;
import io.daoyintech.web.dto.ProductDto;
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
public class ProductServiceImpl implements ProductService {
    @Inject
    ProductRepository productRepository;

    @Inject
    CategoryRepository categoryRepository;

    @Override
    public List<ProductDto> findAll() {
        return this.productRepository.findAll().stream().map(ProductService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ProductDto findById(Long id) {
        return this.productRepository.findById(id).map(ProductService::mapToDto).orElse(null);
    }

    @Override
    public Long countAll() {
        return this.productRepository.count();
    }

    @Override
    public Long countByCategoryId(Long id) {
        return this.productRepository.countAllByCategoryId(id);
    }

    @Override
    public ProductDto create(ProductDto productDto) {
        var product = new Product(
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                ProductStatus.valueOf(productDto.getStatus()),
                productDto.getSalesCounter(),
                Collections.emptySet(),
                categoryRepository.findById(productDto.getCategoryId()).orElse(null)
        );
        this.productRepository.save(product);
        return ProductService.mapToDto(product);
    }

    @Override
    public void delete(Long id) {
        this.productRepository.deleteById(id);
    }

    @Override
    public List<ProductDto> findByCategoryId(Long id) {
        return this.productRepository.findByCategoryId(id).stream().map(ProductService::mapToDto).collect(Collectors.toList());
    }
}
