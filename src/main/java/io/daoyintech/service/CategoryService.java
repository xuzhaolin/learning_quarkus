package io.daoyintech.service;

import io.daoyintech.domain.Category;
import io.daoyintech.web.dto.CategoryDto;
import io.daoyintech.web.dto.ProductDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    static CategoryDto mapToDto(Category category, Long productsCount) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                productsCount
        );
    }

    List<CategoryDto> findAll();
    CategoryDto findById(Long id);
    CategoryDto create(CategoryDto categoryDto);
    void delete(Long id);
    List<ProductDto> findProductsByCategoryId(Long id);

}
