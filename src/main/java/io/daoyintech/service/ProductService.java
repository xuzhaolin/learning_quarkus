package io.daoyintech.service;

import io.daoyintech.domain.Product;
import io.daoyintech.web.dto.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

public interface ProductService {

    static ProductDto mapToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus().name(),
                product.getSalesCounter(),
                product.getReviews().stream().map(ReviewService::mapToDto).collect(Collectors.toSet()),
                product.getCategory().getId()
        );
    }

    List<ProductDto> findAll();
    ProductDto findById(Long id);
    Long countAll();
    Long countByCategoryId(Long id);
    ProductDto create(ProductDto productDto);
    void delete(Long id);
    List<ProductDto> findByCategoryId(Long id);

}
