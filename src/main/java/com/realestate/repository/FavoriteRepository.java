package com.realestate.repository;

import com.realestate.model.Favorite;
import com.realestate.model.Property;
import com.realestate.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findByUser(User user);
    
    List<Favorite> findByUserId(Long userId);
    
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    List<Favorite> findByProperty(Property property);
    
    List<Favorite> findByPropertyId(Long propertyId);
    
    Optional<Favorite> findByUserAndProperty(User user, Property property);
    
    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);
    
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
    
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
    
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.property.id = :propertyId")
    Long countByPropertyId(@Param("propertyId") Long propertyId);
    
    @Query("SELECT f.property FROM Favorite f WHERE f.user.id = :userId")
    List<Property> findFavoritePropertiesByUserId(@Param("userId") Long userId);
}
