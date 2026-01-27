package com.realestate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties", indexes = {
    @Index(name = "idx_city", columnList = "city"),
    @Index(name = "idx_property_type", columnList = "propertyType"),
    @Index(name = "idx_listing_type", columnList = "listingType"),
    @Index(name = "idx_price", columnList = "price")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Column(length = 2000)
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false)
    private BigDecimal price;
    
    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    
    @NotNull(message = "Property type is required")
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;
    
    @NotNull(message = "Listing type is required")
    @Enumerated(EnumType.STRING)
    private ListingType listingType;
    
    @Min(value = 0, message = "Bedrooms cannot be negative")
    private Integer bedrooms;
    
    @Min(value = 0, message = "Bathrooms cannot be negative")
    private Integer bathrooms;
    
    @DecimalMin(value = "0.0", message = "Square footage must be positive")
    private BigDecimal squareFeet;
    
    private Integer yearBuilt;
    
    @Deprecated
    private String imageUrl;  // Keep for backward compatibility
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Many-to-One relationship: Many properties belong to one user (owner/agent)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    @JsonIgnoreProperties({"properties", "hibernateLazyInitializer", "handler"})
    private User owner;
    
    // Many-to-One relationship: Many properties belong to one builder group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "builder_group_id", nullable = true)
    @JsonIgnoreProperties({"properties", "hibernateLazyInitializer", "handler"})
    private BuilderGroup builderGroup;

    // One-to-Many relationship: One property can have multiple images
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"property"})
    private List<PropertyImage> images = new ArrayList<>();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper methods for managing relationships
    public void addImage(PropertyImage image) {
        images.add(image);
        image.setProperty(this);
    }
    
    public void removeImage(PropertyImage image) {
        images.remove(image);
        image.setProperty(null);
    }
}
