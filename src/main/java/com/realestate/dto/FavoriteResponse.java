package com.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {

    private Long id;
    private Long userId;
    private Long propertyId;
    private String notes;
    private LocalDateTime createdAt;

    // Property summary
    private PropertySummary property;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertySummary {
        private Long id;
        private String title;
        private String address;
        private String city;
        private String state;
        private java.math.BigDecimal price;
        private String imageUrl;
    }
}
