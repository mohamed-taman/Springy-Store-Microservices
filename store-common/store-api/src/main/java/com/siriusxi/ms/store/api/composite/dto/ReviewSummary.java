package com.siriusxi.ms.store.api.composite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO convert it to record
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ReviewSummary {
  private final int reviewId;
  private final String author;
  private final String subject;
  private final String content;
}
