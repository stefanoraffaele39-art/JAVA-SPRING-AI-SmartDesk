package com.stefano.smartdesk.ticket;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.stefano.smartdesk.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "comments")
public class Comment {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp 
    private Instant createdAt;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "ticket_id")
    private Ticket ticket;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "author_id")
    private User author;

    public Comment() {
    }

    public Comment(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant cratedAt) {
        this.createdAt = cratedAt;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    
}
