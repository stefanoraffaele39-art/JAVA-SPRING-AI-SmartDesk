package com.stefano.smartdesk.ticket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.stefano.smartdesk.ticket.Comment;
import com.stefano.smartdesk.ticket.dto.CommentResponse;
import com.stefano.smartdesk.ticket.dto.CreateCommentRequest;
import com.stefano.smartdesk.user.mapper.UserMapper;
@Mapper (componentModel = "spring", 
unmappedTargetPolicy = ReportingPolicy.ERROR,
uses = UserMapper.class)
public interface CommentMapper {

    CommentResponse toResponse(Comment comment);


    @Mapping (target = "createdAt", ignore = true)
    @Mapping (target = "author", ignore = true)
    @Mapping (target= "ticket", ignore = true)
    Comment toEntity(CreateCommentRequest request);

    
} 
    
    

    

