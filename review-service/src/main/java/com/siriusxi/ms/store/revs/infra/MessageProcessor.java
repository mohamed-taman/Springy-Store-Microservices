package com.siriusxi.ms.store.revs.infra;

import com.siriusxi.ms.store.api.core.review.ReviewService;
import com.siriusxi.ms.store.api.core.review.dto.Review;
import com.siriusxi.ms.store.api.event.Event;
import com.siriusxi.ms.store.util.exceptions.EventProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import static java.lang.String.valueOf;

@EnableBinding(Sink.class)
@Log4j2
public class MessageProcessor {

    private final ReviewService service;

    @Autowired
    public MessageProcessor(@Qualifier("ReviewServiceImpl") ReviewService service) {
        this.service = service;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Review> event) {

        log.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {
            case CREATE -> {
                Review review = event.getData();
                log.info("Create review with ID: {}/{}", review.getProductId(),
                        review.getReviewId());
                service.createReview(review);
            }
            case DELETE -> {
                int productId = event.getKey();
                log.info("Delete review with Product Id: {}", productId);
                service.deleteReviews(productId);
            }
            default -> {
                String errorMessage =
                        "Incorrect event type: "
                                .concat(valueOf(event.getEventType()))
                                .concat(", expected a CREATE or DELETE event");
                log.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
            }
        }

        log.info("Message processing done!");
    }

}
