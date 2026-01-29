package com.realestate.controller;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.ContactAgentRequest;
import com.realestate.model.ContactAgent;
import com.realestate.service.ContactAgentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact-agents")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001",
                        "http://ec2-3-91-60-245.compute-1.amazonaws.com"})
public class ContactAgentController {

    @Autowired
    private ContactAgentService contactAgentService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContactAgent>> createContact(@Valid @RequestBody ContactAgentRequest request) {
        try {
            ContactAgent contact = contactAgentService.createContact(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Contact created successfully", contact));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Specific paths must come before /{id} to avoid routing conflicts
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<ContactAgent>>> getUnreadContacts() {
        List<ContactAgent> contacts = contactAgentService.getUnreadContacts();
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<List<ContactAgent>>> getContactsByProperty(@PathVariable Long propertyId) {
        try {
            List<ContactAgent> contacts = contactAgentService.getContactsByProperty(propertyId);
            return ResponseEntity.ok(ApiResponse.success(contacts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ContactAgent>>> getContactsByUser(@PathVariable Long userId) {
        try {
            List<ContactAgent> contacts = contactAgentService.getContactsByUser(userId);
            return ResponseEntity.ok(ApiResponse.success(contacts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<ContactAgent>>> getContactsForOwner(@PathVariable Long ownerId) {
        try {
            List<ContactAgent> contacts = contactAgentService.getContactsForPropertyOwner(ownerId);
            return ResponseEntity.ok(ApiResponse.success(contacts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerId}/unread")
    public ResponseEntity<ApiResponse<List<ContactAgent>>> getUnreadContactsForOwner(@PathVariable Long ownerId) {
        try {
            List<ContactAgent> contacts = contactAgentService.getUnreadContactsForOwner(ownerId);
            return ResponseEntity.ok(ApiResponse.success(contacts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/owner/{ownerId}/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@PathVariable Long ownerId) {
        try {
            long count = contactAgentService.getUnreadCountForOwner(ownerId);
            return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Generic {id} path comes last
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactAgent>> getContact(@PathVariable Long id) {
        return contactAgentService.getContactById(id)
                .map(contact -> ResponseEntity.ok(ApiResponse.success(contact)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Contact not found")));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<ContactAgent>> markAsRead(@PathVariable Long id) {
        try {
            ContactAgent contact = contactAgentService.markAsRead(id);
            return ResponseEntity.ok(ApiResponse.success("Contact marked as read", contact));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long id) {
        try {
            contactAgentService.deleteContact(id);
            return ResponseEntity.ok(ApiResponse.success("Contact deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
