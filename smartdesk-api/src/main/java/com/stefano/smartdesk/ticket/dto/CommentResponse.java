package com.stefano.smartdesk.ticket.dto;

import java.time.Instant;

import com.stefano.smartdesk.user.dto.UserSummaryResponse;

public record CommentResponse(
    Long id,
    String content,
    Instant createdAt,
    UserSummaryResponse author
) {
} 
