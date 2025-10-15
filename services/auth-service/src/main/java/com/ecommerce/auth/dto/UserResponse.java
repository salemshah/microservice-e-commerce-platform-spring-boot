package com.ecommerce.auth.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String profileImage;
    private boolean isVerified;
    private Set<String> roles;
}
