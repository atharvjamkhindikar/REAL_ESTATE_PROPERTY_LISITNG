package com.realestate.service;

import com.realestate.dto.FavoriteResponse;
import com.realestate.dto.PageResponse;
import com.realestate.exception.DuplicateResourceException;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.model.Favorite;
import com.realestate.model.Property;
import com.realestate.model.User;
import com.realestate.repository.FavoriteRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    public Favorite addFavorite(Long userId, Long propertyId, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new DuplicateResourceException("Favorite", "userId and propertyId", userId + "," + propertyId);
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        favorite.setNotes(notes);
        
        return favoriteRepository.save(favorite);
    }
    
    public Favorite addFavorite(Long userId, Long propertyId) {
        return addFavorite(userId, propertyId, null);
    }
    
    public void removeFavorite(Long userId, Long propertyId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for user " + userId + " and property " + propertyId));
        favoriteRepository.delete(favorite);
    }
    
    public void removeFavoriteById(Long favoriteId) {
        if (!favoriteRepository.existsById(favoriteId)) {
            throw new ResourceNotFoundException("Favorite", "id", favoriteId);
        }
        favoriteRepository.deleteById(favoriteId);
    }
    
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }
    
    public PageResponse<FavoriteResponse> getUserFavoritesPaged(Long userId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Favorite> favoritePage = favoriteRepository.findByUserId(userId, pageable);

        return buildPageResponse(favoritePage);
    }

    private PageResponse<FavoriteResponse> buildPageResponse(Page<Favorite> page) {
        List<FavoriteResponse> content = page.getContent().stream()
                .map(this::toFavoriteResponse)
                .collect(Collectors.toList());

        return PageResponse.<FavoriteResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    private FavoriteResponse toFavoriteResponse(Favorite favorite) {
        Property property = favorite.getProperty();
        String imageUrl = property.getImages() != null && !property.getImages().isEmpty()
                ? property.getImages().stream()
                    .filter(img -> img.getIsPrimary())
                    .map(img -> img.getImageUrl())
                    .findFirst()
                    .orElse(property.getImages().get(0).getImageUrl())
                : null;

        return FavoriteResponse.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .propertyId(favorite.getProperty().getId())
                .notes(favorite.getNotes())
                .createdAt(favorite.getCreatedAt())
                .property(FavoriteResponse.PropertySummary.builder()
                        .id(property.getId())
                        .title(property.getTitle())
                        .address(property.getAddress())
                        .city(property.getCity())
                        .state(property.getState())
                        .price(property.getPrice())
                        .imageUrl(imageUrl)
                        .build())
                .build();
    }

    public List<Property> getUserFavoriteProperties(Long userId) {
        return favoriteRepository.findFavoritePropertiesByUserId(userId);
    }
    
    public boolean isFavorited(Long userId, Long propertyId) {
        return favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }
    
    public Long getFavoriteCount(Long propertyId) {
        return favoriteRepository.countByPropertyId(propertyId);
    }
    
    public Optional<Favorite> getFavoriteById(Long favoriteId) {
        return favoriteRepository.findById(favoriteId);
    }
    
    public Favorite updateFavoriteNotes(Long favoriteId, String notes) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite", "id", favoriteId));
        favorite.setNotes(notes);
        return favoriteRepository.save(favorite);
    }
    
    public Favorite toggleFavorite(Long userId, Long propertyId) {
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId);
        
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return null; // Removed
        } else {
            return addFavorite(userId, propertyId);
        }
    }
}
