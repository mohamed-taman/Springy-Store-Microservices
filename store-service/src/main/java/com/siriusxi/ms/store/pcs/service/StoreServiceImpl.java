package com.siriusxi.ms.store.pcs.service;

import com.siriusxi.ms.store.api.composite.StoreService;
import com.siriusxi.ms.store.api.composite.dto.ProductAggregate;
import com.siriusxi.ms.store.api.composite.dto.RecommendationSummary;
import com.siriusxi.ms.store.api.composite.dto.ReviewSummary;
import com.siriusxi.ms.store.api.composite.dto.ServiceAddresses;
import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import com.siriusxi.ms.store.api.core.review.dto.Review;
import com.siriusxi.ms.store.pcs.integration.StoreIntegration;
import com.siriusxi.ms.store.util.exceptions.NotFoundException;
import com.siriusxi.ms.store.util.http.ServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// FIXME to add all the checks for empty collections
@Service("StoreServiceImpl")
@Log4j2
public class StoreServiceImpl implements StoreService {

  private final ServiceUtil serviceUtil;
  private final StoreIntegration integration;

  @Autowired
  public StoreServiceImpl(ServiceUtil serviceUtil, StoreIntegration integration) {
    this.serviceUtil = serviceUtil;
    this.integration = integration;
  }

  @Override
  public void createProduct(ProductAggregate body) {

    try {

      log.debug(
          "createCompositeProduct: creates a new composite entity for id: {}", body.getProductId());

      Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
      integration.createProduct(product);

      if (body.getRecommendations() != null) {
        body.getRecommendations()
            .forEach(
                r -> {
                  Recommendation recommendation =
                      new Recommendation(
                          body.getProductId(),
                          r.getRecommendationId(),
                          r.getAuthor(),
                          r.getRate(),
                          r.getContent(),
                          null);
                  integration.createRecommendation(recommendation);
                });
      }

      if (body.getReviews() != null) {
        body.getReviews()
            .forEach(
                r -> {
                  Review review =
                      new Review(
                          body.getProductId(),
                          r.getReviewId(),
                          r.getAuthor(),
                          r.getSubject(),
                          r.getContent(),
                          null);
                  integration.createReview(review);
                });
      }

      log.debug(
          "createCompositeProduct: composite entites created for id: {}", body.getProductId());

    } catch (RuntimeException re) {
      log.warn("createCompositeProduct failed", re);
      throw re;
    }
  }

  @Override
  public ProductAggregate getProduct(int id) {
    log.debug("getCompositeProduct: lookup a product aggregate for id: {}", id);

    Product product = integration.getProduct(id);
    if (product == null) throw new NotFoundException("No product found for id: " + id);

    List<Recommendation> recommendations = integration.getRecommendations(id);

    List<Review> reviews = integration.getReviews(id);

    log.debug("getCompositeProduct: aggregate entity found for id: {}", id);

    return createProductAggregate(
        product, recommendations, reviews, serviceUtil.getServiceAddress());
  }

  @Override
  public void deleteProduct(int id) {

    log.debug("deleteCompositeProduct: Deletes a product aggregate for id: {}", id);

    integration.deleteProduct(id);

    integration.deleteRecommendations(id);

    integration.deleteReviews(id);

    log.debug("getCompositeProduct: aggregate entities deleted for id: {}", id);
  }

  private ProductAggregate createProductAggregate(
      Product product,
      List<Recommendation> recommendations,
      List<Review> reviews,
      String serviceAddress) {

    // 1. Setup product info
    int id = product.getProductId();
    String name = product.getName();
    int weight = product.getWeight();

    // 2. Copy summary recommendation info, if available
    List<RecommendationSummary> recommendationSummaries =
        (recommendations == null)
            ? null
            : recommendations.stream()
                .map(
                    r ->
                        new RecommendationSummary(
                            r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                .collect(Collectors.toList());

    // 3. Copy summary review info, if available
    List<ReviewSummary> reviewSummaries =
        (reviews == null)
            ? null
            : reviews.stream()
                .map(
                    r ->
                        new ReviewSummary(
                            r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                .collect(Collectors.toList());

    // 4. Create info regarding the involved microservices addresses
    String productAddress = product.getServiceAddress();
    String reviewAddress =
        (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
    String recommendationAddress =
        (recommendations != null && recommendations.size() > 0)
            ? recommendations.get(0).getServiceAddress()
            : "";
    ServiceAddresses serviceAddresses =
        new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

    return new ProductAggregate(
        id, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
  }
}
