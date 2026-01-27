package com.realestate.repository;

import com.realestate.model.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    
    List<PropertyImage> findByPropertyId(Long propertyId);
    
    List<PropertyImage> findByPropertyIdOrderByDisplayOrderAsc(Long propertyId);
    
    List<PropertyImage> findByPropertyIdAndIsPrimaryTrue(Long propertyId);

    @Query("SELECT MAX(pi.displayOrder) FROM PropertyImage pi WHERE pi.property.id = :propertyId")
    Integer findMaxDisplayOrderByPropertyId(@Param("propertyId") Long propertyId);
}
