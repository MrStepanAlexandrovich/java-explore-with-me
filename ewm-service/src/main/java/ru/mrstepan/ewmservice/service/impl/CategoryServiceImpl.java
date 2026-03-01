package ru.mrstepan.ewmservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.mrstepan.ewmservice.dao.CategoryRepository;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.dto.NewCategoryDto;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Category;
import ru.mrstepan.ewmservice.mapper.CategoryMapper;
import ru.mrstepan.ewmservice.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {
        log.info("Adding category. Name: {}", dto.getName());
        try {
            Category saved = categoryRepository.save(CategoryMapper.toCategory(dto));
            log.info("Category saved. Id: {}", saved.getId());
            return CategoryMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("Category with name '{}' already exists", dto.getName());
            throw new ConflictException("Category with name '" + dto.getName() + "' already exists");
        }
    }

    @Override
    public void deleteCategory(long id) {
        log.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.error("Category with id={} was not found", id);
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        if (!eventRepository.findAllByCategory_Id(id).isEmpty()) {
            log.error("The category with id={} is not empty", id);
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(id);
        log.info("Category with id: {} deleted", id);
    }

    @Override
    public CategoryDto editCategory(long id, CategoryDto dto) {
        log.info("Editing category with id: {}. New name: {}", id, dto.getName());
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category with id={} was not found", id);
                    return new NotFoundException("Category with id=" + id + " was not found");
                });
        try {
            category.setName(dto.getName());
            Category saved = categoryRepository.save(category);
            log.info("Category with id: {} updated", saved.getId());
            return CategoryMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("Category with name '{}' already exists", dto.getName());
            throw new ConflictException("Category with name '" + dto.getName() + "' already exists");
        }
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Getting categories. from: {}, size: {}", from, size);
        List<CategoryDto> categories = categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
        log.info("Found {} categories", categories.size());
        return categories;
    }

    @Override
    public CategoryDto getCategory(long id) {
        log.info("Getting category by id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category with id={} was not found", id);
                    return new NotFoundException("Category with id=" + id + " was not found");
                });
        log.info("Found category: {}", category.getName());
        return CategoryMapper.toDto(category);
    }
}
