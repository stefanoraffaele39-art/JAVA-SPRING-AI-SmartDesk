package com.stefano.smartdesk.ticket;

import java.net.URI;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stefano.smartdesk.common.dto.PageResponse;
import com.stefano.smartdesk.ticket.dto.AssignTicketRequest;
import com.stefano.smartdesk.ticket.dto.ChangeStatusRequest;
import com.stefano.smartdesk.ticket.dto.CommentResponse;
import com.stefano.smartdesk.ticket.dto.CreateCommentRequest;
import com.stefano.smartdesk.ticket.dto.CreateTicketRequest;
import com.stefano.smartdesk.ticket.dto.TicketResponse;
import com.stefano.smartdesk.ticket.dto.TicketStatsResponse;
import com.stefano.smartdesk.ticket.dto.UpdateTicketRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@Tag (name = "Tickets", description = "Gestione tickets smartdesk")
@RestController 
@RequestMapping ("/api/v1/tickets")
public class TicketController {
    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @Operation (summary = "Ritorna una pagina di ticket in base ai parametri di ricerca")
    @GetMapping ()
    public PageResponse<TicketResponse> search(
        @RequestParam(required = false) TicketStatus status,
        @RequestParam(required = false) TicketPriority priority,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long assigneeId,

        @ParameterObject 
        @PageableDefault (size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ){
        return service.search(status, priority, categoryId, assigneeId, pageable);
    }

    @Operation (summary = "Ritorna il ticket con l'id specificato")
    @GetMapping ("/{id}")
    public TicketResponse findById(@PathVariable Long id){
        return service.findById(id);
    }

    @Operation (summary = "Creazione di un nuovo ticket")
    @PostMapping()
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request ) {
        TicketResponse created = service.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.id())
        .toUri();
        
        return ResponseEntity.created(location).body(created);
    }

    @Operation (summary = "Update di un ticket")
    @PutMapping("/{id}")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTicketRequest request) {
        
        
        return service.update(id, request);
    }

    @Operation (summary = "Delete di un ticket")
    @DeleteMapping ("/{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

    @Operation (summary = "Cambio stato per un ticket")
    @PatchMapping("/{id}/status")
    public TicketResponse changeStatus(@PathVariable Long id,@Valid  @RequestBody ChangeStatusRequest request) {
        
        return service.changeStatus(id, request);
    }

    @Operation (summary = "Assegnazione del ticket")
    @PatchMapping ("/{id}/assignee")
    public TicketResponse assign(@PathVariable Long id,@Valid  @RequestBody AssignTicketRequest request) {
         
        return service.assign(id, request);
    }
    
    @Operation (summary = "Statistiche dei ticket per stato e priorità")
    @GetMapping ("/stats")
    public TicketStatsResponse stats(){
        return service.getStats();
    }

    @Operation (summary = "Ritorna una lista di commenti per uno specifico ticket")
    @GetMapping("/{id}/comments")
    public List<CommentResponse> getComments(@PathVariable Long id) {
        return service.findComments(id);
    }
    
    @Operation (summary = "Crea e aggiungi un commento ad un ticket")
    @PostMapping("/{id}/comments")
    @ResponseStatus (HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable Long id ,@Valid @RequestBody CreateCommentRequest request) {
        return service.addComment(id, request);
    }
    
}
