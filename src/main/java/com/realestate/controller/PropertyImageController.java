package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.PropertyImageRequest;
import com.realestate.dto.PropertyImageResponse;
import com.realestate.service.PropertyImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000")
public class PropertyImageController {

    @Autowired
    private PropertyImageService propertyImageService;

    @PostMapping("/{propertyId}/images")
    public ResponseEntity<ApiResponse<PropertyImageResponse>> addImage(
            @PathVariable Long propertyId,
            @Valid @RequestBody PropertyImageRequest request) {
        try {
            PropertyImageResponse image = propertyImageService.addImage(propertyId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Image added successfully", image));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{propertyId}/images")
    public ResponseEntity<ApiResponse<List<PropertyImageResponse>>> getPropertyImages(@PathVariable Long propertyId) {
        try {
            List<PropertyImageResponse> images = propertyImageService.getPropertyImages(propertyId);
            return ResponseEntity.ok(ApiResponse.success(images));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{propertyId}/images/{imageId}")
    public ResponseEntity<ApiResponse<PropertyImageResponse>> updateImage(
            @PathVariable Long propertyId,
            @PathVariable Long imageId,
            @Valid @RequestBody PropertyImageRequest request) {
        try {
            PropertyImageResponse image = propertyImageService.updateImage(imageId, request);
            return ResponseEntity.ok(ApiResponse.success("Image updated successfully", image));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{propertyId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long propertyId,
            @PathVariable Long imageId) {
        try {
            propertyImageService.deleteImage(imageId);
            return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{propertyId}/images/{imageId}/primary")
    public ResponseEntity<ApiResponse<PropertyImageResponse>> setPrimaryImage(
            @PathVariable Long propertyId,
            @PathVariable Long imageId) {
        try {
            PropertyImageResponse image = propertyImageService.setPrimaryImage(propertyId, imageId);
            return ResponseEntity.ok(ApiResponse.success("Primary image set successfully", image));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{propertyId}/images/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderImages(
            @PathVariable Long propertyId,
            @RequestBody List<Long> imageIds) {
        try {
            propertyImageService.reorderImages(propertyId, imageIds);
            return ResponseEntity.ok(ApiResponse.success("Images reordered successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
