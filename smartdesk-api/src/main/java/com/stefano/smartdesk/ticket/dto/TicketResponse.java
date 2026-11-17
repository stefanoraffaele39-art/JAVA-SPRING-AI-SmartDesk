package com.stefano.smartdesk.ticket.dto;

import java.time.Instant;

import com.stefano.smartdesk.category.dto.CategoryResponse;
import com.stefano.smartdesk.ticket.TicketPriority;
import com.stefano.smartdesk.ticket.TicketStatus;
import com.stefano.smartdesk.user.dto.UserSummaryResponse;

public record TicketResponse(
    Long id,
    String title,
    String description,
    TicketStatus status,
    TicketPriority priority,
    Instant createdAt,
    Instant updatedAt,
    Instant closedAt,
    UserSummaryResponse author,
    UserSummaryResponse assignee,
    CategoryResponse category
) {
    
}
