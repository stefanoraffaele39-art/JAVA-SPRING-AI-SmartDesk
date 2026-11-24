package com.stefano.smartdesk.ticket.dto;

import java.util.Map;

import com.stefano.smartdesk.ticket.TicketPriority;
import com.stefano.smartdesk.ticket.TicketStatus;

public record TicketStatsResponse(
    long total,
    Map<TicketStatus, Long> byStatus,
    Map<TicketPriority, Long> byPriority
) {
    
}
