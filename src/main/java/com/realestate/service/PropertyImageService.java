package com.realestate.service;

import com.realestate.dto.PropertyImageRequest;
import com.realestate.dto.PropertyImageResponse;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.model.Property;
import com.realestate.model.PropertyImage;
import com.realestate.repository.PropertyImageRepository;
import com.realestate.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyImageService {

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    public PropertyImageResponse addImage(Long propertyId, PropertyImageRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        // Get the next display order
        Integer maxOrder = propertyImageRepository.findMaxDisplayOrderByPropertyId(propertyId);
        Integer nextOrder = (maxOrder != null ? maxOrder : -1) + 1;

        PropertyImage image = new PropertyImage();
        image.setImageUrl(request.getImageUrl());
        image.setCaption(request.getCaption());
        image.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : nextOrder);
        image.setIsPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false);
        image.setProperty(property);

        PropertyImage savedImage = propertyImageRepository.save(image);
        return toPropertyImageResponse(savedImage);
    }

    public PropertyImageResponse updateImage(Long imageId, PropertyImageRequest request) {
        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyImage", "id", imageId));

        image.setImageUrl(request.getImageUrl());
        image.setCaption(request.getCaption());
        if (request.getDisplayOrder() != null) {
            image.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsPrimary() != null) {
            image.setIsPrimary(request.getIsPrimary());
        }

        PropertyImage updatedImage = propertyImageRepository.save(image);
        return toPropertyImageResponse(updatedImage);
    }

    public void deleteImage(Long imageId) {
        if (!propertyImageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("PropertyImage", "id", imageId);
        }
        propertyImageRepository.deleteById(imageId);
    }

    public PropertyImageResponse setPrimaryImage(Long propertyId, Long imageId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("PropertyImage", "id", imageId));

        // Verify image belongs to property
        if (!image.getProperty().getId().equals(propertyId)) {
            throw new RuntimeException("Image does not belong to the specified property");
        }

        // Set all images for this property to non-primary
        property.getImages().forEach(img -> img.setIsPrimary(false));

        // Set this image as primary
        image.setIsPrimary(true);
        PropertyImage updated = propertyImageRepository.save(image);

        return toPropertyImageResponse(updated);
    }

    public void reorderImages(Long propertyId, List<Long> imageIds) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        for (int i = 0; i < imageIds.size(); i++) {
            PropertyImage image = propertyImageRepository.findById(imageIds.get(i))
                    .orElseThrow(() -> new ResourceNotFoundException("PropertyImage", "id", imageIds.get(i)));

            if (!image.getProperty().getId().equals(propertyId)) {
                throw new RuntimeException("Image does not belong to the specified property");
            }

            image.setDisplayOrder(i);
            propertyImageRepository.save(image);
        }
    }

    public List<PropertyImageResponse> getPropertyImages(Long propertyId) {
        return propertyImageRepository.findByPropertyIdOrderByDisplayOrderAsc(propertyId)
                .stream()
                .map(this::toPropertyImageResponse)
                .collect(Collectors.toList());
    }

    public PropertyImageResponse toPropertyImageResponse(PropertyImage image) {
        return PropertyImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .caption(image.getCaption())
                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .uploadedAt(image.getUploadedAt())
                .build();
    }
}
