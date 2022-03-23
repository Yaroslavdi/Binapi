package ru.practice.binapi.repository;

import ru.practice.binapi.domain.TODO;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the TODO entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TODORepository extends JpaRepository<TODO, Long> {
}
