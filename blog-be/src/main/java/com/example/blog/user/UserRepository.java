package com.example.blog.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = "posts")
    List<User> findAll();

    @Override
    @EntityGraph(attributePaths = "posts")
    Optional<User> findById(Long id);
}
