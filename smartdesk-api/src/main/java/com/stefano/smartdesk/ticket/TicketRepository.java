package com.stefano.smartdesk.ticket;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long>{
    @Query("SELECT t FROM Ticket t WHERE t.author.id = :id")
    List<Ticket> findByAuthorId(@Param("id") Long id);
}
