package com.stefano.smartdesk.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stefano.smartdesk.category.dto.CategoryRequest;
import com.stefano.smartdesk.category.dto.CategoryResponse;
import com.stefano.smartdesk.category.mapper.CategoryMapper;
import com.stefano.smartdesk.common.exception.BusinessRuleException;
import com.stefano.smartdesk.common.exception.DuplicateResourceException;
import com.stefano.smartdesk.common.exception.ResourceNotFoundException;
import com.stefano.smartdesk.ticket.TicketRepository;

import org.springframework.transaction.annotation.Transactional;
@Service 
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, TicketRepository ticketRepository,
            CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.ticketRepository = ticketRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional (readOnly = true)
    public List<CategoryResponse> findAll(){
        return categoryRepository.findAll().stream()
        .map(categoryMapper::toResponse)
        .toList();
    }

    @Transactional (readOnly = true)
    public CategoryResponse findById(Long id){
        return categoryMapper.toResponse(getCategoryOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request){
        if(categoryRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Esiste giá una categoria con nome " + request.name());
        }

        Category saved = categoryRepository.save(categoryMapper.toEntity(request));
        return categoryMapper.toResponse(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request){
        Category category = getCategoryOrThrow(id);

        if(categoryRepository.existsByNameAndIdNot(request.name(), id)){
            throw new DuplicateResourceException("Esiste giá una categoria con nome: " + request.name());
        }

        categoryMapper.updateEntity(request, category);
        return categoryMapper.toResponse(category);
    }

    public void delete(Long id){
        Category category = getCategoryOrThrow(id);
        if(ticketRepository.existsByCategoryId(id)){
            throw new BusinessRuleException("Impossibile eliminare la categoria: HA TICKET COLLEGATI");
        }

        categoryRepository.delete(category);
    }

    private Category getCategoryOrThrow(Long id){
        return (categoryRepository.findById(id)). 
        orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
    }

}
