package com.siriusxi.ms.store.api.composite.dto;

/**
 * Record <code>ServiceAddresses</code> that hold all services addresses involved in the product
 * call.
 *
 * @implNote Since it is a record and not normal POJO, so it needs some customizations
 * to be serialized to JSON and this is done with method
 * <code>GlobalConfiguration.jacksonCustomizer()</code>.
 *
 * @see java.lang.Record
 * @see com.siriusxi.ms.store.util.config.GlobalConfiguration
 * @author mohamed.taman
 * @version v4.6
 * @since v0.1
 */
public class ServiceAddresses {
    private String storeService;
    private String productService;
    private String reviewService;
    private String recommendationService;

    public ServiceAddresses(String storeService, String productService, String reviewService, String recommendationService) {
        this.storeService = storeService;
        this.productService = productService;
        this.reviewService = reviewService;
        this.recommendationService = recommendationService;
    }

    public String getStoreService() {
        return storeService;
    }

    public void setStoreService(String storeService) {
        this.storeService = storeService;
    }

    public String getProductService() {
        return productService;
    }

    public void setProductService(String productService) {
        this.productService = productService;
    }

    public String getReviewService() {
        return reviewService;
    }

    public void setReviewService(String reviewService) {
        this.reviewService = reviewService;
    }

    public String getRecommendationService() {
        return recommendationService;
    }

    public void setRecommendationService(String recommendationService) {
        this.recommendationService = recommendationService;
    }

    // You can also override toString(), equals(), and hashCode() methods if needed
}
