package io.daoyintech.service;

import io.daoyintech.domain.Review;
import io.daoyintech.web.dto.ReviewDto;

import java.util.List;

public interface ReviewService {

    static ReviewDto mapToDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getTitle(),
                review.getDescription(),
                review.getRating()
        );
    }

    List<ReviewDto> findReviewsByProductId(Long id);
    ReviewDto findById(Long id);
    ReviewDto create(ReviewDto reviewDto, Long productId);
    void delete(Long reviewId);
}
