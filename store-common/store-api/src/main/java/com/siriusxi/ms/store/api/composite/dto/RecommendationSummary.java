package com.siriusxi.ms.store.api.composite.dto;

/**
 * Record <code>RecommendationSummary</code> that hold all the product recommendations.
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
public record RecommendationSummary(int recommendationId,
                                    String author,
                                    int rate,
                                    String content) {
}