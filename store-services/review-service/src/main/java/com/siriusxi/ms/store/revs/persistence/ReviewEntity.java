package com.siriusxi.ms.store.revs.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(
        name = "review",
    indexes = {
      @Index(name = "review_unique_idx", unique = true, columnList = "productId, reviewId")
    })
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ReviewEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;

  @Version private int version;

  @NonNull
  private int productId;
  @NonNull
  private int reviewId;
  @NonNull
  @NotBlank
  @Size(min = 6, max = 50)
  private String author;
  @NonNull
  @NotBlank
  private String subject;
  @NonNull
  @NotBlank
  private String content;
}
