package com.siriusxi.ms.store.api.composite.dto;

import java.util.List;

/**
 * Class <code>ProductAggregate</code> that holds all the product aggregate information.
 *
 * @implNote This class needs some customizations to be serialized to JSON,
 * which can be done using method <code>GlobalConfiguration.jacksonCustomizer()</code>.
 *
 * @see com.siriusxi.ms.store.util.config.GlobalConfiguration
 * @author mohamed.taman
 * @version v4.6
 * @since v0.1
 */
public class ProductAggregate {

    private final int productId;
    private final String name;
    private final int weight;
    private final List<RecommendationSummary> recommendations;
    private final List<ReviewSummary> reviews;
    private final ServiceAddresses serviceAddresses;

    // Constructor
    public ProductAggregate(int productId,
                            String name,
                            int weight,
                            List<RecommendationSummary> recommendations,
                            List<ReviewSummary> reviews,
                            ServiceAddresses serviceAddresses) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.recommendations = recommendations;
        this.reviews = reviews;
        this.serviceAddresses = serviceAddresses;
    }

    // Getter methods

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public List<RecommendationSummary> getRecommendations() {
        return recommendations;
    }

    public List<ReviewSummary> getReviews() {
        return reviews;
    }

    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }

    // Other methods as needed
}
