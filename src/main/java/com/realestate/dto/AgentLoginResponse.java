package com.realestate.dto;

import com.realestate.model.Role;
import com.realestate.model.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentLoginResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String company;
    private String licenseNumber;
    private String bio;
    private String profileImageUrl;
    private Role role;
    private SubscriptionType subscriptionType;
    private Boolean active;
    private LocalDateTime createdAt;
}
