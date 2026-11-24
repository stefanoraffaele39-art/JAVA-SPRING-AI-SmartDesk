package com.stefano.smartdesk.ticket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.stefano.smartdesk.category.mapper.CategoryMapper;
import com.stefano.smartdesk.ticket.Ticket;
import com.stefano.smartdesk.ticket.dto.CreateTicketRequest;
import com.stefano.smartdesk.ticket.dto.TicketResponse;
import com.stefano.smartdesk.ticket.dto.UpdateTicketRequest;
import com.stefano.smartdesk.user.mapper.UserMapper;

@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {UserMapper.class, CategoryMapper.class})
public interface TicketMapper {

    TicketResponse toResponse(Ticket ticket);

    
    @Mapping (target = "status", ignore = true)
    @Mapping (target = "createdAt", ignore = true)
    @Mapping (target = "updatedAt", ignore = true)
    @Mapping (target = "closedAt", ignore = true)
    @Mapping (target = "author", ignore = true)
    @Mapping (target = "assignee", ignore = true)
    @Mapping (target = "category", ignore = true)
    @Mapping (target = "comments", ignore = true)   
    Ticket toEntity(CreateTicketRequest request);

    
    @Mapping (target = "status", ignore = true)
    @Mapping (target = "createdAt", ignore = true)
    @Mapping (target = "updatedAt", ignore = true)
    @Mapping (target = "closedAt", ignore = true)
    @Mapping (target = "author", ignore = true)
    @Mapping (target = "assignee", ignore = true)
    @Mapping (target = "category", ignore = true)
    @Mapping (target = "comments", ignore = true)
    void updateEntity(UpdateTicketRequest request, @MappingTarget Ticket ticket);

} 