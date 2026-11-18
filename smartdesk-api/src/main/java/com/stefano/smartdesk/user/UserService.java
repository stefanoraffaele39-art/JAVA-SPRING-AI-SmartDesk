package com.stefano.smartdesk.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stefano.smartdesk.common.dto.PageResponse;
import com.stefano.smartdesk.common.exception.BusinessRuleException;
import com.stefano.smartdesk.common.exception.DuplicateResourceException;
import com.stefano.smartdesk.common.exception.ResourceNotFoundException;
import com.stefano.smartdesk.ticket.CommentRepository;
import com.stefano.smartdesk.ticket.TicketRepository;
import com.stefano.smartdesk.user.dto.UserRequest;
import com.stefano.smartdesk.user.dto.UserResponse;
import com.stefano.smartdesk.user.dto.UserSummaryResponse;
import com.stefano.smartdesk.user.mapper.UserMapper;

@Service 
@Transactional 
public class UserService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TicketRepository ticketRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, TicketRepository ticketRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional (readOnly = true)
    public PageResponse<UserResponse> findAll(Pageable pageable){
        Page<UserResponse> page = userRepository.findAll(pageable)
        .map(userMapper::toResponse);

        return PageResponse.from(page);
    }

    @Transactional (readOnly = true)
    public PageResponse<UserSummaryResponse> findAllByRole(UserRole role, Pageable pageable){
        Page<UserSummaryResponse> page = userRepository.findAllUserByRole(role, pageable)
        .map(userMapper::toSummary);
        return PageResponse.from(page);
    }

    @Transactional (readOnly = true)
    public UserResponse findById(Long id){
        return userMapper.toResponse(getUserOrThrow(id));
    }

    public UserResponse create(UserRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("Esiste giá un utente con email: " + request.email());
        }

        User saved = userRepository.save(userMapper.toEntity(request));

        return userMapper.toResponse(saved);
    }

    public UserResponse update(Long id, UserRequest request){
        User user = getUserOrThrow(id);

        if(userRepository.existsByEmailAndIdNot(request.email(), id)){
            throw new DuplicateResourceException("Esiste giá un utente con email: " + request.email());
        }

        userMapper.updateEntity(request, user);
        return userMapper.toResponse(user);
    }

    public void delete(Long id){
        User user = getUserOrThrow(id);

        if(ticketRepository.existsByAuthorIdOrAssigneeId(id, id)){
            throw new BusinessRuleException("Impossibile eliminare l'utente: é autore o assegnatario di almeno un ticket");
        }

        if(commentRepository.existsByAuthorId(id)){
            throw new BusinessRuleException("Impossibile eliminare l'utente: ha scritto dei commenti");
        }

        userRepository.delete(user);
    }

    private User getUserOrThrow(Long id){
        return (userRepository.findById(id))
        .orElseThrow(() -> new ResourceNotFoundException("Utente", id));
    }
}
