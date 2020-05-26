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
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.github.resilience4j.reactor.retry.RetryExceptionWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;


import static org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext;

@Service("StoreServiceImpl")
@Log4j2
public class StoreServiceImpl implements StoreService {

  private final ServiceUtil serviceUtil;
  private final StoreIntegration integration;
  private final SecurityContext nullSC = new SecurityContextImpl();

  @Autowired
  public StoreServiceImpl(ServiceUtil serviceUtil, StoreIntegration integration) {
    this.serviceUtil = serviceUtil;
    this.integration = integration;
  }

  @Override
  public Mono<Void> createProduct(ProductAggregate body) {
    return getContext().doOnSuccess(sc -> createProductImpl(sc, body)).then();
  }

  private void createProductImpl(SecurityContext sc, ProductAggregate body) {
    try {

      log.debug(
          "createProduct: creates a new composite entity for productId: {}", body.productId());

      logAuthorizationInfo(sc);

      integration.createProduct(new Product(body.productId(), body.name(), body.weight(), null));

      if (body.recommendations() != null && !body.recommendations().isEmpty()) {
        body.recommendations()
            .forEach(
                r ->
                    integration.createRecommendation(
                        new Recommendation(
                            body.productId(),
                            r.recommendationId(),
                            r.author(),
                            r.rate(),
                            r.content(),
                            null)));
      }

      if (body.reviews() != null && !body.reviews().isEmpty()) {
        body.reviews()
            .forEach(
                r ->
                    integration.createReview(
                        new Review(
                            body.productId(),
                            r.reviewId(),
                            r.author(),
                            r.subject(),
                            r.content(),
                            null)));
      }
      log.debug("createProduct: composite entities created for productId: {}", body.productId());

    } catch (RuntimeException re) {
      log.warn("createProduct failed: {}", re.toString());
      throw re;
    }
  }

  @Override
  public Mono<ProductAggregate> getProduct(int productId, int delay, int faultPercent) {
    return Mono.zip(
            values ->
                createProductAggregate(
                    (SecurityContext) values[0],
                    (Product) values[1],
                    (List<Recommendation>) values[2],
                    (List<Review>) values[3],
                    serviceUtil.getServiceAddress()),
            getContext().defaultIfEmpty(nullSC),
            integration
                .getProduct(productId, delay, faultPercent)
                    .onErrorMap(RetryExceptionWrapper.class, Throwable::getCause)
                    .onErrorReturn(CircuitBreakerOpenException.class,
                            getProductFallbackValue(productId)),
            integration.getRecommendations(productId).collectList(),
            integration.getReviews(productId).collectList())
        .doOnError(ex -> log.warn("getProduct failed: {}", ex.toString()))
        .log();
  }

  @Override
  public Mono<Void> deleteProduct(int productId) {
    return getContext().doOnSuccess(sc -> deleteProductImpl(sc, productId)).then();
  }

  private void deleteProductImpl(SecurityContext sc, int productId) {
    try {

      log.debug("deleteProduct: Deletes a product aggregate for productId: {}", productId);
      logAuthorizationInfo(sc);

      integration.deleteProduct(productId);
      integration.deleteRecommendations(productId);
      integration.deleteReviews(productId);

      log.debug("deleteProduct: aggregate entities deleted for productId: {}", productId);

    } catch (RuntimeException re) {
      log.warn("deleteProduct failed: {}", re.toString());
      throw re;
    }
  }

  private Product getProductFallbackValue(int productId) {

    log.warn("Creating a fallback product for productId = {}", productId);

    if (productId == 14) {
      String errMsg = "Product Id: " + productId + " not found in fallback cache!";
      log.warn(errMsg);
      throw new NotFoundException(errMsg);
    }

    return new Product(
        productId, "Fallback product" + productId, productId, serviceUtil.getServiceAddress());
  }

  private ProductAggregate createProductAggregate(
      SecurityContext sc,
      Product product,
      List<Recommendation> recommendations,
      List<Review> reviews,
      String serviceAddress) {

    logAuthorizationInfo(sc);

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
        (reviews != null && !reviews.isEmpty()) ? reviews.get(0).getServiceAddress() : "";
    String recommendationAddress =
        (recommendations != null && !recommendations.isEmpty())
            ? recommendations.get(0).getServiceAddress()
            : "";
    ServiceAddresses serviceAddresses =
        new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

    return new ProductAggregate(
        productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
  }

  /*
   * Methods logAuthorizationInfo(SecurityContext sc),
   * and     logAuthorizationInfo(Jwt jwt)
   * has been added to log relevant parts from the JWT-encoded
   * access token upon each call to the API.
   *
   * The access token can be acquired using the standard Spring Security, SecurityContext,
   * which, in a reactive environment, can be acquired using the static helper method,
   * ReactiveSecurityContextHolder.getContext().
   *
   */
  private void logAuthorizationInfo(SecurityContext sc) {
    if (sc != null
        && sc.getAuthentication() != null
        && sc.getAuthentication() instanceof JwtAuthenticationToken) {
      Jwt jwtToken = ((JwtAuthenticationToken) sc.getAuthentication()).getToken();
      logAuthorizationInfo(jwtToken);
    } else {
      log.warn("No JWT based Authentication supplied, running tests are we?");
    }
  }

  private void logAuthorizationInfo(Jwt jwt) {
    if (jwt == null) {
      log.warn("No JWT supplied, running tests are we?");
    } else {
      if (log.isDebugEnabled()) {
        URL issuer = jwt.getIssuer();
        List<String> audience = jwt.getAudience();
        Object subject = jwt.getClaims().get("sub");
        Object scopes = jwt.getClaims().get("scope");
        Object expires = jwt.getClaims().get("exp");

        log.debug(
            "Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}",
            subject,
            scopes,
            expires,
            issuer,
            audience);
      }
    }
  }
}
