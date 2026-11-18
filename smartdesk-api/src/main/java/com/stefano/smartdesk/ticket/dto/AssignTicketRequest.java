package com.stefano.smartdesk.ticket.dto;

import jakarta.validation.constraints.NotNull;

public record AssignTicketRequest(
    @NotNull Long assigneeId
) {
    
}
