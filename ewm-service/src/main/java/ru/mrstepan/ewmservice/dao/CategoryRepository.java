package ru.mrstepan.ewmservice.dao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.ewmservice.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndIdNot(@NotBlank @Size(min = 1, max = 50) String name, long id);
}
