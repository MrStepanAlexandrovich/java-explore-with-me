package ru.mrstepan.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mrstepan.ewmservice.dao.CategoryRepository;
import ru.mrstepan.ewmservice.dto.CategoryDto;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Category;
import ru.mrstepan.ewmservice.model.CategoryMapper;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public void addCategory(CategoryDto categoryDto) {
        log.info("Adding category. Name: {}", categoryDto.getName());
        Category category = CategoryMapper.toCategory(categoryDto);

        Category category1 = categoryRepository.save(category);

        log.info("{} category saved. Id: {}", category1.getName(), category1.getId());
    }

    @Override
    public void deleteCategory(long id) {
        log.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
        log.info("Category with id: {} was deleted", id);
    }

    @Override
    public void editCategory(long id, CategoryDto categoryDto) {
        log.info("Editing category with id: {}. New name: {}", id, categoryDto.getName());

        log.trace("Finding category with id: {}", id);
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Category with id: {} wasn't found", id);
                    return new NotFoundException("Category with id: " + id + " wasn't found");
                }
        );

        log.info("Category name: {}, id: {} was found", category.getName(), category.getId());

        Category category1 = CategoryMapper.toCategory(categoryDto);
        category1.setId(id);

        categoryRepository.save(category1);
    }

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        return List.of();
    }

    @Override
    public CategoryDto getCategory(long id) {
        log.info("Getting category with id: {}", id);
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Category with id: {} wasn't found", id);
                    return new NotFoundException("Category with id: " + id + " wasn't found");
                }
        );

        log.info("Category name: {}, id: {} was found", category.getName(), category.getId());
        return CategoryMapper.toDto(category);
    }
}
