package com.siriusxi.ms.store.api.core.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
  private int productId;
  private int reviewId;
  private String author;
  private String subject;
  private String content;
  private String serviceAddress;
}
