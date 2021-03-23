package io.daoyintech.service.impl;

import io.daoyintech.domain.Review;
import io.daoyintech.repository.ProductRepository;
import io.daoyintech.repository.ReviewRepository;
import io.daoyintech.service.ReviewService;
import io.daoyintech.web.dto.ReviewDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {
    @Inject
    ProductRepository productRepository;

    @Inject
    ReviewRepository reviewRepository;

    @Override
    public List<ReviewDto> findReviewsByProductId(Long id) {
        return this.reviewRepository.findReviewsByProductId(id).stream().map(ReviewService::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ReviewDto findById(Long id) {
        return this.reviewRepository.findById(id).map(ReviewService::mapToDto).orElse(null);
    }

    @Override
    public ReviewDto create(ReviewDto reviewDto, Long productId) {
        var product = this.productRepository.findById(productId).orElseThrow(() -> new IllegalStateException("Product with ID:" + productId + " was not found"));
        var savedReview = new Review(
                reviewDto.getTitle(),
                reviewDto.getDescription(),
                reviewDto.getRating()
        );
        this.reviewRepository.saveAndFlush(savedReview);
        product.getReviews().add(savedReview);
        this.productRepository.saveAndFlush(product);
        return ReviewService.mapToDto(savedReview);
    }

    @Override
    public void delete(Long reviewId) {
        var review = this.reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalStateException("Product with ID:" +reviewId + " was not found !"));
        var product = this.productRepository.findProductByReviewId(reviewId);
        product.getReviews().remove(review);
        this.productRepository.saveAndFlush(product);
        this.reviewRepository.delete(review);
    }
}
