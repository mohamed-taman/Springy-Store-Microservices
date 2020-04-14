package com.siriusxi.ms.store.api.core.review;

import com.siriusxi.ms.store.api.core.review.dto.Review;

import java.util.List;

/**
 * Interface that define the general service contract (methods) for the Review
 * <ol>
 *     <li>Service and,</li>
 *     <li>Controller interfaces.</li>
 * </ol>
 *
 * @author mohamed.taman
 * @version v0.2
 * @since v0.1
 */
public interface ReviewService {


    /**
     * Get all reviews for specific product by product id.
     *
     * @param productId that you are looking for its reviews.
     * @return list of reviews for this product,
     * or empty list if there are no reviews.
     */
    List<Review> getReviews(int productId);

    /**
     * Create a new review for a product.
     *
     * @param body review to be created.
     * @return just created review.
     */
    Review createReview(Review body);

    /**
     * Delete all product reviews.
     *
     * @param productId to delete its reviews.
     */
    void deleteReviews(int productId);
}
