package com.stefano.smartdesk.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.stefano.smartdesk.user.User;
import com.stefano.smartdesk.user.dto.UserRequest;
import com.stefano.smartdesk.user.dto.UserResponse;
import com.stefano.smartdesk.user.dto.UserSummaryResponse;

@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    
    UserResponse toResponse(User user);

    UserSummaryResponse toSummary(User user);

    @Mapping (target = "createdAt", ignore = true)
    User toEntity(UserRequest request);

    @Mapping (target = "createdAt", ignore = true)
    void updateEntity(UserRequest request, @MappingTarget User user);
}
