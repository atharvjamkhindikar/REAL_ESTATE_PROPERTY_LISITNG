package com.realestate.service;

import com.realestate.dto.PageResponse;
import com.realestate.dto.ScheduleViewingRequest;
import com.realestate.exception.ResourceNotFoundException;
import com.realestate.model.Property;
import com.realestate.model.ScheduleViewing;
import com.realestate.model.User;
import com.realestate.model.ViewingStatus;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.ScheduleViewingRepository;
import com.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleViewingService {

    @Autowired
    private ScheduleViewingRepository scheduleViewingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    public ScheduleViewing scheduleViewing(ScheduleViewingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", request.getPropertyId()));

        // Validate that the viewing date is in the future
        if (request.getViewingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Viewing date must be in the future");
        }

        // Check for existing pending/confirmed viewings on the same date
        List<ScheduleViewing> conflicts = scheduleViewingRepository.findConflictingViewings(
                request.getPropertyId(),
                request.getViewingDate()
        );

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Property already has viewings scheduled for this date");
        }

        ScheduleViewing viewing = new ScheduleViewing();
        viewing.setUser(user);
        viewing.setProperty(property);
        viewing.setViewingDate(request.getViewingDate());
        viewing.setViewingTime(request.getViewingTime());
        viewing.setNotes(request.getNotes());
        viewing.setStatus(ViewingStatus.PENDING);

        return scheduleViewingRepository.save(viewing);
    }

    public Optional<ScheduleViewing> getViewingById(Long id) {
        return scheduleViewingRepository.findById(id);
    }

    public List<ScheduleViewing> getUserViewings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return scheduleViewingRepository.findByUserIdOrderByViewingDateAsc(userId);
    }

    public PageResponse<ScheduleViewing> getUserViewingsPaged(Long userId, int page, int size, String sortBy, String direction) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ScheduleViewing> viewingsPage = scheduleViewingRepository.findByUserId(userId, pageable);

        return buildPageResponse(viewingsPage);
    }

    private PageResponse<ScheduleViewing> buildPageResponse(Page<ScheduleViewing> page) {
        List<ScheduleViewing> content = page.getContent();

        return PageResponse.<ScheduleViewing>builder()
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

    public List<ScheduleViewing> getPropertyViewings(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        return scheduleViewingRepository.findByPropertyIdOrderByViewingDateAsc(propertyId);
    }

    public List<ScheduleViewing> getViewingsForOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        return scheduleViewingRepository.findAllViewingsForPropertyOwner(ownerId);
    }

    public List<ScheduleViewing> getViewingsForOwnerByStatus(Long ownerId, ViewingStatus status) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        return scheduleViewingRepository.findViewingsForPropertyOwnerByStatus(ownerId, status);
    }

    public List<ScheduleViewing> getUserViewingsByStatus(Long userId, ViewingStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return scheduleViewingRepository.findByUserIdAndStatus(userId, status);
    }

    public List<ScheduleViewing> getPropertyViewingsByStatus(Long propertyId, ViewingStatus status) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        return scheduleViewingRepository.findByPropertyIdAndStatus(propertyId, status);
    }

    public List<ScheduleViewing> getViewingsInDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleViewingRepository.findViewingsInDateRange(startDate, endDate);
    }

    public ScheduleViewing confirmViewing(Long viewingId) {
        ScheduleViewing viewing = scheduleViewingRepository.findById(viewingId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleViewing", "id", viewingId));

        if (!viewing.getStatus().equals(ViewingStatus.PENDING)) {
            throw new IllegalArgumentException("Only pending viewings can be confirmed");
        }

        viewing.setStatus(ViewingStatus.CONFIRMED);
        viewing.setConfirmedAt(LocalDateTime.now());

        return scheduleViewingRepository.save(viewing);
    }

    public ScheduleViewing rejectViewing(Long viewingId, String rejectionReason) {
        ScheduleViewing viewing = scheduleViewingRepository.findById(viewingId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleViewing", "id", viewingId));

        if (!viewing.getStatus().equals(ViewingStatus.PENDING)) {
            throw new IllegalArgumentException("Only pending viewings can be rejected");
        }

        viewing.setStatus(ViewingStatus.REJECTED);
        viewing.setRejectionReason(rejectionReason);
        viewing.setRejectedAt(LocalDateTime.now());

        return scheduleViewingRepository.save(viewing);
    }

    public ScheduleViewing completeViewing(Long viewingId) {
        ScheduleViewing viewing = scheduleViewingRepository.findById(viewingId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleViewing", "id", viewingId));

        if (!viewing.getStatus().equals(ViewingStatus.CONFIRMED)) {
            throw new IllegalArgumentException("Only confirmed viewings can be marked as completed");
        }

        viewing.setStatus(ViewingStatus.COMPLETED);
        viewing.setCompletedAt(LocalDateTime.now());

        return scheduleViewingRepository.save(viewing);
    }

    public ScheduleViewing cancelViewing(Long viewingId) {
        ScheduleViewing viewing = scheduleViewingRepository.findById(viewingId)
                .orElseThrow(() -> new ResourceNotFoundException("ScheduleViewing", "id", viewingId));

        if (viewing.getStatus().equals(ViewingStatus.COMPLETED) || viewing.getStatus().equals(ViewingStatus.REJECTED)) {
            throw new IllegalArgumentException("Cannot cancel " + viewing.getStatus() + " viewings");
        }

        viewing.setStatus(ViewingStatus.CANCELLED);
        viewing.setCancelledAt(LocalDateTime.now());

        return scheduleViewingRepository.save(viewing);
    }

    public void deleteViewing(Long viewingId) {
        if (!scheduleViewingRepository.existsById(viewingId)) {
            throw new ResourceNotFoundException("ScheduleViewing", "id", viewingId);
        }
        scheduleViewingRepository.deleteById(viewingId);
    }

    public long getConfirmedViewingCountForProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id", propertyId));

        return scheduleViewingRepository.countConfirmedViewingsForProperty(propertyId);
    }
}
