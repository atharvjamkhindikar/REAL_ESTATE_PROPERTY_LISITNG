package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.PageResponse;
import com.realestate.dto.PropertyResponse;
import com.realestate.dto.PropertySearchRequest;
import com.realestate.model.ListingType;
import com.realestate.model.Property;
import com.realestate.model.PropertyType;
import com.realestate.service.PropertyService;
import com.realestate.service.SearchHistoryService;
import com.realestate.service.DotNetRecommendationClient;
import com.realestate.repository.PropertyRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001",
                        "http://ec2-3-91-60-245.compute-1.amazonaws.com"})
public class PropertyController {
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private SearchHistoryService searchHistoryService;
    
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DotNetRecommendationClient dotNetRecommendationClient;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Property>>> getAllProperties() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getAllProperties()));
    }
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Property>>> getAvailableProperties() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getAvailableProperties()));
    }

    @GetMapping("/available/paged")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> getAvailablePropertiesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        PageResponse<PropertyResponse> properties = propertyService.getAvailablePropertiesPaged(page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(property -> ResponseEntity.ok(ApiResponse.success(property)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Property not found with id: " + id)));
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<PropertyResponse>> getPropertyDetails(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        try {
            PropertyResponse response = propertyService.getPropertyResponseById(id, userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Property>> createProperty(@Valid @RequestBody Property property) {
        Property createdProperty = propertyService.createProperty(property);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Property created successfully", createdProperty));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Property>> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody Property property) {
        try {
            Property updatedProperty = propertyService.updateProperty(id, property);
            return ResponseEntity.ok(ApiResponse.success("Property updated successfully", updatedProperty));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id) {
        try {
            // Check if property exists first
            if (!propertyRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Property not found with id: " + id));
            }

            // Delete the property (cascade will handle related records)
            propertyService.deleteProperty(id);

            return ResponseEntity.ok(ApiResponse.success("Property deleted successfully", null));
        } catch (DataIntegrityViolationException e) {
            // Handle foreign key constraint violations
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Cannot delete property: It has related records. Please delete related items first."));
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting property: " + e.getMessage()));
        }
    }
    
    // Advanced Search
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> searchProperties(
            @RequestBody PropertySearchRequest searchRequest,
            @RequestParam(required = false) Long userId) {
        PageResponse<PropertyResponse> results = propertyService.searchProperties(searchRequest, userId);
        
        // Save search history if user is logged in
        if (userId != null) {
            searchHistoryService.saveSearchHistory(userId, searchRequest, (int) results.getTotalElements());
        }
        
        return ResponseEntity.ok(ApiResponse.success(results));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> searchPropertiesGet(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Integer maxBedrooms,
            @RequestParam(required = false) Integer minBathrooms,
            @RequestParam(required = false) Integer maxBathrooms,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId) {
        
        PropertySearchRequest searchRequest = PropertySearchRequest.builder()
                .keyword(keyword)
                .city(city)
                .state(state)
                .propertyType(propertyType)
                .listingType(listingType)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minBedrooms(minBedrooms)
                .maxBedrooms(maxBedrooms)
                .minBathrooms(minBathrooms)
                .maxBathrooms(maxBathrooms)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();
        
        PageResponse<PropertyResponse> results = propertyService.searchProperties(searchRequest, userId);
        
        // Save search history if user is logged in
        if (userId != null) {
            searchHistoryService.saveSearchHistory(userId, searchRequest, (int) results.getTotalElements());
        }
        
        return ResponseEntity.ok(ApiResponse.success(results));
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<Property>>> getPropertiesByCity(@PathVariable String city) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPropertiesByCity(city)));
    }
    
    @GetMapping("/type/{propertyType}")
    public ResponseEntity<ApiResponse<List<Property>>> getPropertiesByType(@PathVariable PropertyType propertyType) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPropertiesByType(propertyType)));
    }
    
    @GetMapping("/listing-type/{listingType}")
    public ResponseEntity<ApiResponse<List<Property>>> getPropertiesByListingType(@PathVariable ListingType listingType) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPropertiesByListingType(listingType)));
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<Property>>> getPropertiesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPropertiesByPriceRange(minPrice, maxPrice)));
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<Property>>> getPropertiesByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPropertiesByOwner(ownerId)));
    }
    
    @GetMapping("/owner/{ownerId}/paged")
    public ResponseEntity<ApiResponse<PageResponse<PropertyResponse>>> getPropertiesByOwnerPaged(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPropertiesByOwnerPaged(ownerId, page, size)));
    }
    
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<Property>>> getRecentProperties(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getRecentProperties(limit)));
    }
    
    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<String>>> getAllCities() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getAllCities()));
    }
    
    @GetMapping("/states")
    public ResponseEntity<ApiResponse<List<String>>> getAllStates() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getAllStates()));
    }
    
    @GetMapping("/stats/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getAvailableCount() {
        Long count = propertyService.getAvailableCount();
        return ResponseEntity.ok(ApiResponse.success(Map.of("availableCount", count)));
    }
    
    @GetMapping("/stats/average-price/{city}")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getAveragePriceByCity(@PathVariable String city) {
        BigDecimal avgPrice = propertyService.getAveragePriceByCity(city);
        return ResponseEntity.ok(ApiResponse.success(Map.of("averagePrice", avgPrice != null ? avgPrice : BigDecimal.ZERO)));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<Property>>> recommend(@RequestParam String location,
                                                                  @RequestParam double budget) {
        List<Property> allProperties = propertyRepository.findAll();
        List<Property> recommendations = dotNetRecommendationClient.getRecommendations(allProperties, location, budget);
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }

    @GetMapping("/convert-price")
    public ResponseEntity<ApiResponse<BigDecimal>> convertPriceToINR(@RequestParam BigDecimal priceInUSD) {
        BigDecimal conversionRate = new BigDecimal("82.50"); // Example conversion rate
        BigDecimal priceInINR = priceInUSD.multiply(conversionRate);
        return ResponseEntity.ok(ApiResponse.success(priceInINR));
    }
}
