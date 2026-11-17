package com.stefano.smartdesk.ticket.dto;

import com.stefano.smartdesk.ticket.TicketStatus;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
    @NotNull TicketStatus status
) {
    
}
