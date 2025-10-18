package com.ecommerce.product.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private String userId;
    private int rating;
    private String comment;
    private boolean verifiedPurchase;
    private LocalDateTime createdAt;
}
