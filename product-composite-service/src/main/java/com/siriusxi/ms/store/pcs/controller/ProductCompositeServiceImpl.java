package com.siriusxi.ms.store.pcs.controller;

import com.siriusxi.ms.store.api.composite.product.ProductCompositeService;
import com.siriusxi.ms.store.api.composite.product.dto.ProductAggregate;
import com.siriusxi.ms.store.api.composite.product.dto.RecommendationSummary;
import com.siriusxi.ms.store.api.composite.product.dto.ReviewSummary;
import com.siriusxi.ms.store.api.composite.product.dto.ServiceAddresses;
import com.siriusxi.ms.store.api.core.product.Product;
import com.siriusxi.ms.store.api.core.recommendation.Recommendation;
import com.siriusxi.ms.store.api.core.review.Review;
import com.siriusxi.ms.store.pcs.integration.ProductCompositeIntegration;
import com.siriusxi.ms.store.util.exceptions.NotFoundException;
import com.siriusxi.ms.store.util.http.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

    private final ServiceUtil serviceUtil;
    private final ProductCompositeIntegration integration;

    @Autowired
    public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public ProductAggregate getProduct(int productId) {

        var product = integration.getProduct(productId);
        if (product == null)
            throw new NotFoundException("No product found for productId: " + productId);

        var recommendations = integration.getRecommendations(productId);

        var reviews = integration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations,
                                                    List<Review> reviews, String serviceAddress) {

        // 1. Setup product info
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate()))
                        .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
                reviews.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && !reviews.isEmpty()) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && !recommendations.isEmpty()) ?
                recommendations.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(
                serviceAddress,
                productAddress,
                reviewAddress,
                recommendationAddress);

        return new ProductAggregate(
                productId,
                name,
                weight,
                recommendationSummaries,
                reviewSummaries,
                serviceAddresses);
    }
}
