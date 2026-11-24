package com.stefano.smartdesk.ticket;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long>{
    
    boolean existsByAuthorId(Long id);

    @EntityGraph(attributePaths = "author")
    List<Comment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
