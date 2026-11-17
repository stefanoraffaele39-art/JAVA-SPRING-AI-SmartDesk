package com.stefano.smartdesk.ticket.dto;

import com.stefano.smartdesk.ticket.TicketPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
    @NotBlank @Size(max = 200) String title,
    @NotBlank String description,
    @NotNull TicketPriority priority,
    @NotNull Long authorId,
    @NotNull Long categoryId
) {
    
}
