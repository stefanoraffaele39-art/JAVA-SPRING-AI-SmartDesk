package com.stefano.smartdesk.user.dto;

import java.time.Instant;

import com.stefano.smartdesk.user.UserRole;

public record UserResponse(
    Long id, 
    String fullName, 
    String email, 
    UserRole role, 
    Instant createdAt) {
    
}
