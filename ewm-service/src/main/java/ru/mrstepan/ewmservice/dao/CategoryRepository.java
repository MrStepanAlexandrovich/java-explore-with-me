package ru.mrstepan.ewmservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.ewmservice.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
