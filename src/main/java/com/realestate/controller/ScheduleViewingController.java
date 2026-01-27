package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.PageResponse;
import com.realestate.dto.ScheduleViewingRequest;
import com.realestate.model.ScheduleViewing;
import com.realestate.model.ViewingStatus;
import com.realestate.service.ScheduleViewingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule-viewings")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduleViewingController {

    @Autowired
    private ScheduleViewingService scheduleViewingService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleViewing>> scheduleViewing(@Valid @RequestBody ScheduleViewingRequest request) {
        try {
            ScheduleViewing viewing = scheduleViewingService.scheduleViewing(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Viewing scheduled successfully", viewing));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Specific paths must come before /{id} to avoid routing conflicts
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<ScheduleViewing>>> getViewingsInDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            List<ScheduleViewing> viewings = scheduleViewingService.getViewingsInDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(viewings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ScheduleViewing>>> getUserViewings(@PathVariable Long userId) {
        try {
            List<ScheduleViewing> viewings = scheduleViewingService.getUserViewings(userId);
            return ResponseEntity.ok(ApiResponse.success(viewings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<PageResponse<ScheduleViewing>>> getUserViewingsPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "viewingDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            PageResponse<ScheduleViewing> viewingsPage = scheduleViewingService.getUserViewingsPaged(userId, page, size, sortBy, direction);
            return ResponseEntity.ok(ApiResponse.success(viewingsPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<List<ScheduleViewing>>> getUserViewingsByStatus(
            @PathVariable Long userId,
            @PathVariable ViewingStatus status) {
        try {
            List<ScheduleViewing> viewings = scheduleViewingService.getUserViewingsByStatus(userId, status);
            return ResponseEntity.ok(ApiResponse.success(viewings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<ScheduleViewing>>> getPropertyViewings(@PathVariable Long propertyId) {
        try {
            List<ScheduleViewing> viewings = scheduleViewingService.getPropertyViewings(propertyId);
            return ResponseEntity.ok(ApiResponse.success(viewings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/property/{propertyId}/status/{status}")
    public ResponseEntity<ApiResponse<List<ScheduleViewing>>> getPropertyViewingsByStatus(
            @PathVariable Long propertyId,
            @PathVariable ViewingStatus status) {
        try {
            List<ScheduleViewing> viewings = scheduleViewingService.getPropertyViewingsByStatus(propertyId, status);
            return ResponseEntity.ok(ApiResponse.success(viewings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/property/{propertyId}/confirmed-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getConfirmedViewingCount(@PathVariable Long propertyId) {
        try {
            long count = scheduleViewingService.getConfirmedViewingCountForProperty(propertyId);
            return ResponseEntity.ok(ApiResponse.success(Map.of("confirmedCount", count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<ScheduleViewing>>> getViewingsForOwner(@PathVariable Long ownerId) {
        try {
            List<ScheduleViewing> viewings = scheduleViewingService.getViewingsForOwner(ownerId);
            return ResponseEntity.ok(ApiResponse.success(viewings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerId}/status/{status}")
    public ResponseEntity<ApiResponse<List<ScheduleViewing>>> getViewingsForOwnerByStatus(
            @PathVariable Long ownerId,
            @PathVariable ViewingStatus status) {
        try {
            List<ScheduleViewing> viewings = scheduleViewingService.getViewingsForOwnerByStatus(ownerId, status);
            return ResponseEntity.ok(ApiResponse.success(viewings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Generic {id} path comes last
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleViewing>> getViewing(@PathVariable Long id) {
        return scheduleViewingService.getViewingById(id)
                .map(viewing -> ResponseEntity.ok(ApiResponse.success(viewing)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Viewing not found")));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<ScheduleViewing>> confirmViewing(@PathVariable Long id) {
        try {
            ScheduleViewing viewing = scheduleViewingService.confirmViewing(id);
            return ResponseEntity.ok(ApiResponse.success("Viewing confirmed successfully", viewing));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ScheduleViewing>> rejectViewing(
            @PathVariable Long id,
            @RequestParam(required = false) String rejectionReason) {
        try {
            ScheduleViewing viewing = scheduleViewingService.rejectViewing(id, rejectionReason);
            return ResponseEntity.ok(ApiResponse.success("Viewing rejected successfully", viewing));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ScheduleViewing>> completeViewing(@PathVariable Long id) {
        try {
            ScheduleViewing viewing = scheduleViewingService.completeViewing(id);
            return ResponseEntity.ok(ApiResponse.success("Viewing marked as completed", viewing));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ScheduleViewing>> cancelViewing(@PathVariable Long id) {
        try {
            ScheduleViewing viewing = scheduleViewingService.cancelViewing(id);
            return ResponseEntity.ok(ApiResponse.success("Viewing cancelled successfully", viewing));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteViewing(@PathVariable Long id) {
        try {
            scheduleViewingService.deleteViewing(id);
            return ResponseEntity.ok(ApiResponse.success("Viewing deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
