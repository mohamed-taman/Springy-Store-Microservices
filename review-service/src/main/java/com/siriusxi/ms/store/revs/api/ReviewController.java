package com.siriusxi.ms.store.revs.api;

import com.siriusxi.ms.store.api.core.product.ProductEndpoint;
import com.siriusxi.ms.store.api.core.review.ReviewEndpoint;
import com.siriusxi.ms.store.api.core.review.ReviewService;
import com.siriusxi.ms.store.api.core.review.dto.Review;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Class <code>ReviewController</code> is the implementation of the main Review Endpoint API
 * definition.
 *
 * @see ReviewEndpoint
 * @author mohamed.taman
 * @version v4.0
 * @since v3.0 codename Storm
 */
@RestController
@Log4j2
public class ReviewController implements ReviewEndpoint {

  /** Review service business logic interface. */
  private final ReviewService reviewService;

  @Autowired
  public ReviewController(@Qualifier("ReviewServiceImpl") ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  /** {@inheritDoc} */
  @Override
  public Flux<Review> getReviews(int productId) {
    return reviewService.getReviews(productId);
  }
}
