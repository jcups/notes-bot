package ru.jcups.notesbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jcups.notesbot.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoriesByUserId(long id);
}
