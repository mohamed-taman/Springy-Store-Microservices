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
import com.siriusxi.ms.store.util.http.ServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

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
          "createCompositeProduct: creates a new composite entity for productId: {}",
          body.getProductId());

      var product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
      integration.createProduct(product);

      if (body.getRecommendations() != null) {
        body.getRecommendations()
            .forEach(
                r -> {
                  var recommendation =
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
          "createCompositeProduct: composite entities created for productId: {}",
          body.getProductId());

    } catch (RuntimeException re) {
      log.warn("createCompositeProduct failed: {}", re.toString());
      throw re;
    }
  }

  @Override
  public Mono<ProductAggregate> getProduct(int productId) {
    return Mono.zip(
            values ->
                createProductAggregate(
                    (Product) values[0],
                    (List<Recommendation>) values[1],
                    (List<Review>) values[2],
                    serviceUtil.getServiceAddress()),
            integration.getProduct(productId),
            integration.getRecommendations(productId).collectList(),
            integration.getReviews(productId).collectList())
        .doOnError(ex -> log.warn("getCompositeProduct failed: {}", ex.toString()))
        .log();
  }

  @Override
  public void deleteProduct(int productId) {

    try {

      log.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

      integration.deleteProduct(productId);
      integration.deleteRecommendations(productId);
      integration.deleteReviews(productId);

      log.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);

    } catch (RuntimeException re) {
      log.warn("deleteCompositeProduct failed: {}", re.toString());
      throw re;
    }
  }

  private ProductAggregate createProductAggregate(
      Product product,
      List<Recommendation> recommendations,
      List<Review> reviews,
      String serviceAddress) {

    // 1. Setup product info
    int productId = product.getProductId();
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
        productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
  }
}
