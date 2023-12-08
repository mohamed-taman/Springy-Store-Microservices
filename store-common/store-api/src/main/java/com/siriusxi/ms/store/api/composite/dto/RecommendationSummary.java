package com.siriusxi.ms.store.api.composite.dto;

/**
 * Class <code>RecommendationSummary</code> that holds all the product recommendations.
 *
 * @implNote This class is a replacement for the record and should be used for serialization to JSON.
 *
 * @see com.siriusxi.ms.store.util.config.GlobalConfiguration
 * @author mohamed.taman
 * @version v4.6
 * @since v0.1
 */
public class RecommendationSummary {

    private final int recommendationId;
    private final String author;
    private final int rate;
    private final String content;

    public RecommendationSummary(int recommendationId, String author, int rate, String content) {
        this.recommendationId = recommendationId;
        this.author = author;
        this.rate = rate;
        this.content = content;
    }

    public int getRecommendationId() {
        return recommendationId;
    }

    public String getAuthor() {
        return author;
    }

    public int getRate() {
        return rate;
    }

    public String getContent() {
        return content;
    }

    // You can add other methods as needed

    @Override
    public String toString() {
        return "RecommendationSummary{" +
                "recommendationId=" + recommendationId +
                ", author='" + author + '\'' +
                ", rate=" + rate +
                ", content='" + content + '\'' +
                '}';
    }
}
