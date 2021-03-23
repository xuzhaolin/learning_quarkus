package io.daoyintech.service.impl;

import io.daoyintech.domain.Category;
import io.daoyintech.repository.CategoryRepository;
import io.daoyintech.repository.ProductRepository;
import io.daoyintech.service.CategoryService;
import io.daoyintech.service.ProductService;
import io.daoyintech.web.dto.CategoryDto;
import io.daoyintech.web.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@Transactional
public class CategoryServiceImpl implements CategoryService {
   @Inject
    CategoryRepository categoryRepository;

   @Inject
    ProductRepository productRepository;

    @Override
    public List<CategoryDto> findAll() {
        log.debug("Request to get all categories");
        return this.categoryRepository.findAll().stream().map(category -> CategoryService.mapToDto(category, productRepository.countAllByCategoryId(category.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long id) {
        log.debug("Request to get Category : {}", id);
        return this.categoryRepository.findById(id)
                .map(category -> CategoryService.mapToDto(category, productRepository.countAllByCategoryId(category.getId()))).orElseThrow(() -> new IllegalStateException("No category"));
    }

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        log.debug("Request to create Category");
        var category = new Category(categoryDto.getName(), categoryDto.getDescription());
        this.categoryRepository.save(category);
        return CategoryService.mapToDto(category, 0L);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete category: {}", id);
        log.debug("Delete all products for the Category : {}", id);
        this.productRepository.deleteAllByCategoryId(id);
        log.debug("Deleting Category: {}", id);
        this.categoryRepository.deleteById(id);
    }

    @Override
    public List<ProductDto> findProductsByCategoryId(Long id) {
        return this.productRepository.findAllByCategoryId(id).stream().map(ProductService::mapToDto).collect(Collectors.toList());
    }
}
