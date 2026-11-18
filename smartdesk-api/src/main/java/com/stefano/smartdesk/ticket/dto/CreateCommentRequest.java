package com.stefano.smartdesk.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
    @NotNull Long authorId,
    @NotBlank String content
) {
    
}
