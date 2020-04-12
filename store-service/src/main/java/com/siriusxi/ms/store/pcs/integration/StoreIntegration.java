package com.siriusxi.ms.store.pcs.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siriusxi.ms.store.api.core.product.Product;
import com.siriusxi.ms.store.api.core.product.ProductService;
import com.siriusxi.ms.store.api.core.recommendation.Recommendation;
import com.siriusxi.ms.store.api.core.recommendation.RecommendationService;
import com.siriusxi.ms.store.api.core.review.Review;
import com.siriusxi.ms.store.api.core.review.ReviewService;
import com.siriusxi.ms.store.util.exceptions.InvalidInputException;
import com.siriusxi.ms.store.util.exceptions.NotFoundException;
import com.siriusxi.ms.store.util.http.HttpErrorInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static org.springframework.http.HttpMethod.GET;

@Component
@Log4j2
public class StoreIntegration
        implements
        ProductService,
        RecommendationService,
        ReviewService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    @Autowired
    public StoreIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,

            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        var http = "http://";

        productServiceUrl = http.concat(productServiceHost).concat(":").concat(valueOf(productServicePort))
                .concat("/products/");
        recommendationServiceUrl = http.concat(recommendationServiceHost).concat(":")
                .concat(valueOf(recommendationServicePort)).concat("/recommendations");
        reviewServiceUrl = http.concat(reviewServiceHost).concat(":").concat(valueOf(reviewServicePort))
                .concat("/reviews");
    }

    @Override
    public Product createProduct(Product body) {

        try {
            String url = productServiceUrl;
            log.debug("Will post a new product to URL: {}", url);

            Product product = restTemplate.postForObject(url, body, Product.class);
            log.debug("Created a product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Product getProduct(int productId) {

        try {
            String url = productServiceUrl + "/" + productId;
            log.debug("Will call the getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            log.debug("Found a product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            log.debug("Will call the deleteProduct API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {

        try {
            String url = recommendationServiceUrl;
            log.debug("Will post a new recommendation to URL: {}", url);

            Recommendation recommendation = restTemplate.postForObject(url, body, Recommendation.class);
            log.debug("Created a recommendation with id: {}", recommendation.getProductId());

            return recommendation;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        try {
            String url = recommendationServiceUrl + "?productId=" + productId;

            log.debug("Will call the getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {}).getBody();

            log.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;

        } catch (Exception ex) {
            log.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;
            log.debug("Will call the deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Review createReview(Review body) {

        try {
            String url = reviewServiceUrl;
            log.debug("Will post a new review to URL: {}", url);

            Review review = restTemplate.postForObject(url, body, Review.class);
            log.debug("Created a review with id: {}", review.getProductId());

            return review;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Review> getReviews(int productId) {

        try {
            String url = reviewServiceUrl + "?productId=" + productId;

            log.debug("Will call the getReviews API on URL: {}", url);
            List<Review> reviews = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {}).getBody();

            log.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;

        } catch (Exception ex) {
            log.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            log.debug("Will call the deleteReviews API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(ex));
            default:
                log.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                log.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).message();
        } catch (IOException ioException) {
            return ex.getMessage();
        }
    }
}
