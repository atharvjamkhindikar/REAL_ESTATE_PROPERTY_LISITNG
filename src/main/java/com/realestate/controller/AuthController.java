package com.realestate.controller;


import com.realestate.dto.*;
import com.realestate.model.User;
import com.realestate.model.UserType;
import com.realestate.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001",
                        "http://ec2-3-91-60-245.compute-1.amazonaws.com"})
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegistrationRequest request) {
        try {
            // Validate password confirmation
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Passwords do not match"));
            }

            // Validate license number for AGENT type
            if (request.getUserType() == UserType.AGENT) {
                if (request.getLicenseNumber() == null || request.getLicenseNumber().trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error("License number is required for agent registration"));
                }
            }


            // Convert to UserRequest and create user
            UserRequest userRequest = UserRequest.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .phone(request.getPhone())
                    .userType(request.getUserType())
                    .licenseNumber(request.getLicenseNumber())
                    .company(request.getCompany())
                    .bio(request.getBio())
                    .subscriptionType(request.getSubscriptionType()) // Set subscriptionType
                    .build();

            User user = userService.createUser(userRequest);
            UserResponse response = userService.toUserResponse(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userService.getUserByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        }
        
        User user = userOptional.get();
        
        // Check password (in production, use BCrypt or similar)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        }
        
        // Check if user is active
        if (!user.getActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Your account has been deactivated. Please contact support."));
        }
        
        LoginResponse loginResponse = LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .role(user.getRole())
                .subscriptionType(user.getSubscriptionType())
                .active(user.getActive())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }
    
    @GetMapping("/me/{id}")
    public ResponseEntity<ApiResponse<LoginResponse>> getCurrentUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }
        
        User user = userOptional.get();
        
        LoginResponse loginResponse = LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .role(user.getRole())
                .subscriptionType(user.getSubscriptionType())
                .active(user.getActive())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping("/agent-login")
    public ResponseEntity<ApiResponse<AgentLoginResponse>> agentLogin(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userService.getUserByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        }

        User user = userOptional.get();

        // Verify user is an AGENT
        if (user.getUserType() != UserType.AGENT) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("This login is for agents only. Please use regular login."));
        }

        // Check password (in production, use BCrypt or similar)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        }

        // Check if user is active
        if (!user.getActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Your account has been deactivated. Please contact support."));
        }

        AgentLoginResponse agentResponse = AgentLoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .company(user.getCompany())
                .licenseNumber(user.getLicenseNumber())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .subscriptionType(user.getSubscriptionType())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Agent login successful", agentResponse));
    }
}
