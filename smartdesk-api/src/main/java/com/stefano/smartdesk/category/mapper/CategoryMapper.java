package com.stefano.smartdesk.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.stefano.smartdesk.category.Category;
import com.stefano.smartdesk.category.dto.CategoryRequest;
import com.stefano.smartdesk.category.dto.CategoryResponse;

@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryRequest request);

    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
