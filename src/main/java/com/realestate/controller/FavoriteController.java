package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.FavoriteResponse;
import com.realestate.dto.PageResponse;
import com.realestate.model.Favorite;
import com.realestate.model.Property;
import com.realestate.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:3000")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Favorite>>> getUserFavorites(@PathVariable Long userId) {
        List<Favorite> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(ApiResponse.success(favorites));
    }
    
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<PageResponse<FavoriteResponse>>> getUserFavoritesPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            PageResponse<FavoriteResponse> favoritesPage = favoriteService.getUserFavoritesPaged(userId, page, size, sortBy, direction);
            return ResponseEntity.ok(ApiResponse.success(favoritesPage));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/properties")
    public ResponseEntity<ApiResponse<List<Property>>> getUserFavoriteProperties(@PathVariable Long userId) {
        List<Property> properties = favoriteService.getUserFavoriteProperties(userId);
        return ResponseEntity.ok(ApiResponse.success(properties));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Favorite>> addFavorite(
            @RequestParam Long userId,
            @RequestParam Long propertyId,
            @RequestParam(required = false) String notes) {
        try {
            Favorite favorite = favoriteService.addFavorite(userId, propertyId, notes);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Property added to favorites", favorite));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @RequestParam Long userId,
            @RequestParam Long propertyId) {
        try {
            favoriteService.removeFavorite(userId, propertyId);
            return ResponseEntity.ok(ApiResponse.success("Property removed from favorites", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<Void>> removeFavoriteById(@PathVariable Long favoriteId) {
        try {
            favoriteService.removeFavoriteById(favoriteId);
            return ResponseEntity.ok(ApiResponse.success("Favorite removed successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Long propertyId) {
        boolean isFavorited = favoriteService.isFavorited(userId, propertyId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("isFavorited", isFavorited)));
    }
    
    @GetMapping("/count/{propertyId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getFavoriteCount(@PathVariable Long propertyId) {
        Long count = favoriteService.getFavoriteCount(propertyId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("favoriteCount", count)));
    }
    
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<Favorite>> toggleFavorite(
            @RequestParam Long userId,
            @RequestParam Long propertyId) {
        try {
            Favorite favorite = favoriteService.toggleFavorite(userId, propertyId);
            if (favorite == null) {
                return ResponseEntity.ok(ApiResponse.success("Property removed from favorites", null));
            }
            return ResponseEntity.ok(ApiResponse.success("Property added to favorites", favorite));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PatchMapping("/{favoriteId}/notes")
    public ResponseEntity<ApiResponse<Favorite>> updateFavoriteNotes(
            @PathVariable Long favoriteId,
            @RequestParam String notes) {
        try {
            Favorite favorite = favoriteService.updateFavoriteNotes(favoriteId, notes);
            return ResponseEntity.ok(ApiResponse.success("Notes updated successfully", favorite));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
