package com.realestate.dto;

import com.realestate.model.UserType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "User type is required")
    private UserType userType;

    // For AGENT registration
    @Pattern(regexp = "^[A-Z]{2}-\\d{5}$|^$", message = "License number must be in format XX-12345 (e.g., RE-12345) or empty for non-agents")
    private String licenseNumber;

    private String company;
    private String bio;
}
