package ru.mrstepan.ewmservice.service;

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
import ru.mrstepan.ewmservice.model.CategoryMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
            throw new ConflictException("Category with name '" + dto.getName() + "' already exists");
        }
    }

    @Override
    public void deleteCategory(long id) {
        log.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        if (!eventRepository.findAllByCategory_Id(id).isEmpty()) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto editCategory(long id, CategoryDto dto) {
        log.info("Editing category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        try {
            category.setName(dto.getName());
            return CategoryMapper.toDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category with name '" + dto.getName() + "' already exists");
        }
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
        return CategoryMapper.toDto(category);
    }
}
