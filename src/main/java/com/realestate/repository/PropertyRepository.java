package com.realestate.repository;

import com.realestate.model.ListingType;
import com.realestate.model.Property;
import com.realestate.model.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    
    List<Property> findByAvailableTrue();
    
    Page<Property> findByAvailableTrue(Pageable pageable);
    
    List<Property> findByCity(String city);
    
    List<Property> findByCityIgnoreCase(String city);
    
    List<Property> findByPropertyType(PropertyType propertyType);
    
    List<Property> findByListingType(ListingType listingType);
    
    List<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Property> findByCityAndPropertyType(String city, PropertyType propertyType);
    
    List<Property> findByOwnerId(Long ownerId);
    
    Page<Property> findByOwnerId(Long ownerId, Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.available = true AND " +
           "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(p.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
           "(:listingType IS NULL OR p.listingType = :listingType) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms) AND " +
           "(:maxBedrooms IS NULL OR p.bedrooms <= :maxBedrooms) AND " +
           "(:minBathrooms IS NULL OR p.bathrooms >= :minBathrooms) AND " +
           "(:maxBathrooms IS NULL OR p.bathrooms <= :maxBathrooms) AND " +
           "(:minSquareFeet IS NULL OR p.squareFeet >= :minSquareFeet) AND " +
           "(:maxSquareFeet IS NULL OR p.squareFeet <= :maxSquareFeet)")
    Page<Property> searchProperties(
            @Param("city") String city,
            @Param("state") String state,
            @Param("propertyType") PropertyType propertyType,
            @Param("listingType") ListingType listingType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minBedrooms") Integer minBedrooms,
            @Param("maxBedrooms") Integer maxBedrooms,
            @Param("minBathrooms") Integer minBathrooms,
            @Param("maxBathrooms") Integer maxBathrooms,
            @Param("minSquareFeet") BigDecimal minSquareFeet,
            @Param("maxSquareFeet") BigDecimal maxSquareFeet,
            Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.available = true AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Property> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT DISTINCT p.city FROM Property p WHERE p.available = true ORDER BY p.city")
    List<String> findAllCities();
    
    @Query("SELECT DISTINCT p.state FROM Property p WHERE p.available = true ORDER BY p.state")
    List<String> findAllStates();
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.available = true")
    Long countAvailable();
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.propertyType = :type AND p.available = true")
    Long countByPropertyType(@Param("type") PropertyType type);
    
    @Query("SELECT AVG(p.price) FROM Property p WHERE p.city = :city AND p.available = true")
    BigDecimal getAveragePriceByCity(@Param("city") String city);
    
    List<Property> findByBuilderGroupId(Long builderGroupId);

    Page<Property> findByBuilderGroupId(Long builderGroupId, Pageable pageable);

    List<Property> findByBuilderGroupIdAndAvailableTrue(Long builderGroupId);

    Page<Property> findByBuilderGroupIdAndAvailableTrue(Long builderGroupId, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE p.available = true AND p.builderGroup.id = :builderGroupId ORDER BY p.createdAt DESC")
    List<Property> findAvailablePropertiesByBuilderGroup(@Param("builderGroupId") Long builderGroupId, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE p.available = true AND " +
           "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:builderGroupId IS NULL OR p.builderGroup.id = :builderGroupId) AND " +
           "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Property> searchPropertiesByBuilderGroup(
            @Param("city") String city,
            @Param("builderGroupId") Long builderGroupId,
            @Param("propertyType") PropertyType propertyType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("SELECT p FROM Property p WHERE p.available = true ORDER BY p.createdAt DESC")
    List<Property> findRecentProperties(Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.available = true AND p.listingType = 'FOR_SALE' ORDER BY p.price ASC")
    List<Property> findCheapestForSale(Pageable pageable);
}
