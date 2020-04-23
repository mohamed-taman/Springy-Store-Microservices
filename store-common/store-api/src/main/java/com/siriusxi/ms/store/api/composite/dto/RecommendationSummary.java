package com.siriusxi.ms.store.api.composite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO convert it to record
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RecommendationSummary {
  private final int recommendationId;
  private final String author;
  private final int rate;
  private final String content;
}
