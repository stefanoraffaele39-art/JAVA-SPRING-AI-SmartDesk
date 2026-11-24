package com.stefano.smartdesk.ticket;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stefano.smartdesk.category.Category;
import com.stefano.smartdesk.category.CategoryRepository;
import com.stefano.smartdesk.common.dto.PageResponse;
import com.stefano.smartdesk.common.exception.BusinessRuleException;
import com.stefano.smartdesk.common.exception.ResourceNotFoundException;
import com.stefano.smartdesk.ticket.dto.AssignTicketRequest;
import com.stefano.smartdesk.ticket.dto.ChangeStatusRequest;
import com.stefano.smartdesk.ticket.dto.CreateTicketRequest;
import com.stefano.smartdesk.ticket.dto.TicketResponse;
import com.stefano.smartdesk.ticket.dto.TicketStatsResponse;
import com.stefano.smartdesk.ticket.dto.UpdateTicketRequest;
import com.stefano.smartdesk.ticket.mapper.TicketMapper;
import com.stefano.smartdesk.user.User;
import com.stefano.smartdesk.user.UserRepository;
import com.stefano.smartdesk.user.UserRole;


@Service 
@Transactional 
public class TicketService {
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public TicketService(CategoryRepository categoryRepository, TicketRepository ticketRepository,
            UserRepository userRepository, TicketMapper ticketMapper) {
        this.categoryRepository = categoryRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
    }

    @Transactional (readOnly = true)
    public PageResponse<TicketResponse> search(TicketStatus status, TicketPriority priority,
        Long categoryId, Long assigneeId, Pageable pageable){
            Page<TicketResponse> page = ticketRepository
            .search(status, priority, categoryId, assigneeId, pageable)
            .map(ticketMapper::toResponse);

            return PageResponse.from(page);
        }

    @Transactional (readOnly = true)
    public TicketResponse findById(Long id){
        return ticketMapper.toResponse(getTicketOrThrow(id));
    }

    public TicketResponse create(CreateTicketRequest request){
        Ticket ticket = ticketMapper.toEntity(request);
        ticket.setAuthor(getUserOrThrow(request.authorId()));
        ticket.setCategory(getCategoryOrThrow(request.categoryId()));
        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse update(Long id, UpdateTicketRequest request){
        Ticket ticket = getTicketOrThrow(id);
        ticketMapper.updateEntity(request, ticket);
        ticket.setCategory(getCategoryOrThrow(request.categoryId()));
        return ticketMapper.toResponse(ticket);
    }

    public void delete(Long id){
        ticketRepository.delete(getTicketOrThrow(id));
    }

    public TicketResponse changeStatus(Long id, ChangeStatusRequest request){
        Ticket ticket = getTicketOrThrow(id);
        TicketStatus next = request.status();

        if(!ticket.getStatus().canTransitionTo(next)){
            throw new BusinessRuleException("Passaggio di stato non consentito: " + ticket.getStatus() + " → " + next);
        }

        ticket.setStatus(next);
        if(next == TicketStatus.CLOSED){
            ticket.setClosedAt(Instant.now());
        }

        return ticketMapper.toResponse(ticket);
    }

    public TicketResponse assign(Long id, AssignTicketRequest request){
        Ticket ticket = getTicketOrThrow(id);
        User assignee = getUserOrThrow(request.assigneeId());

        if(assignee.getRole() != UserRole.AGENT){
            throw new BusinessRuleException("Solo un utente AGENT può essere assegnatario");
        }

        ticket.setAssignee(assignee);
        return ticketMapper.toResponse(ticket);
    }

    public TicketStatsResponse getStats(){
        Map<TicketStatus, Long> byStatus = new EnumMap<>(TicketStatus.class);
        for(TicketStatus status : TicketStatus.values()){
            byStatus.put(status, 0L);
        }

        ticketRepository.countByStatus()
        .forEach(row -> byStatus.put(row.getStatus(), row.getTotal()));

        Map<TicketPriority, Long> byPriority = new EnumMap<>(TicketPriority.class);
        for(TicketPriority priority : TicketPriority.values()){
            byPriority.put(priority, 0L);
        }

        ticketRepository.countByPriority()
        .forEach(row -> byPriority.put(row.getPriority(), row.getTotal()));

        long total = byStatus.values().stream().mapToLong(Long::longValue).sum();

        return new TicketStatsResponse(total, byStatus, byPriority);
    }


    //helpers

    private Ticket getTicketOrThrow(Long id){
        return ticketRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", id));
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
    }
}
