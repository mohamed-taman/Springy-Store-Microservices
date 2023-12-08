package com.siriusxi.ms.store.api.composite.dto;

import java.util.Objects;

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
public class ReviewSummary {
    private int reviewId;
    private String author;
    private String subject;
    private String content;

    public ReviewSummary(int reviewId, String author, String subject, String content) {
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ReviewSummary that = (ReviewSummary) obj;
        return reviewId == that.reviewId &&
                Objects.equals(author, that.author) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, author, subject, content);
    }

    @Override
    public String toString() {
        return "ReviewSummary{" +
                "reviewId=" + reviewId +
                ", author='" + author + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

