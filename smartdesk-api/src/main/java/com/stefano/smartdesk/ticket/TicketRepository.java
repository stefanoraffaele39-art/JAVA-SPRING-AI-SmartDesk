package com.stefano.smartdesk.ticket;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

    @Override 
    @EntityGraph (attributePaths = {"author", "assignee", "category"})
    Optional<Ticket> findById(Long id);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByAuthorIdOrAssigneeId(Long authorId, Long assigneeId);

    @EntityGraph (attributePaths = {"author", "assignee", "category"})
    @Query ("""
            SELECT t FROM Ticket t
            LEFT JOIN t.assignee a
            WHERE (:status IS NULL OR t.status = :status)
            AND (:priority IS NULL OR t.priority = :priority)
            AND (:categoryId IS NULL OR t.category.id = :categoryId)
            AND (:assigneeId IS NULL OR a.id = :assigneeId)
        """)
    Page<Ticket> search(
        @Param("status") TicketStatus status,
        @Param("priority") TicketPriority priority,
        @Param("categoryId") Long categoryId,
        @Param("assigneeId") Long assigneeId,
        Pageable pageable);

    @Query("SELECT t.status AS status, COUNT(t) AS total FROM Ticket t GROUP BY t.status")
    List<StatusCount> countByStatus();
    
    @Query("SELECT t.priority AS priority, COUNT(t) AS total FROM Ticket t GROUP BY t.priority")
    List<PriorityCount> countByPriority();

    interface StatusCount {TicketStatus getStatus(); long getTotal();}
    interface PriorityCount {TicketPriority getPriority(); long getTotal();}
}
