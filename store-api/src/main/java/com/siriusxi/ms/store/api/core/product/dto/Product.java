package com.siriusxi.ms.store.api.core.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
  private int productId;
  private String name;
  private int weight;
  private String serviceAddress;
}
