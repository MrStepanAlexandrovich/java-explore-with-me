package ru.mrstepan.ewmservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.ewmservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
