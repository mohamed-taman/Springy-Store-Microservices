package com.siriusxi.ms.store.api.composite.dto;

/**
 * Record <code>ReviewSummary</code> that hold all product reviews.
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
public record ReviewSummary(int reviewId,
                            String author,
                            String subject,
                            String content){
}
