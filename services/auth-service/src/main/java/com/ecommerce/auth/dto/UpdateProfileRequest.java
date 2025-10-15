package com.ecommerce.auth.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstname;

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastname;

    @Size(max = 50, message = "Phone number must not exceed 50 characters")
    private String phoneNumber;

    @Size(max = 255, message = "Profile image URL too long")
    private String profileImage;
}
