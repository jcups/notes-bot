package ru.jcups.notesbot.service;

import org.springframework.stereotype.Service;
import ru.jcups.notesbot.entity.Category;
import ru.jcups.notesbot.entity.User;
import ru.jcups.notesbot.repo.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public boolean isExists(long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    public List<Category> getCategoriesByUserId(long userId){
        return categoryRepository.findCategoriesByUserId(userId);
    }

    public void addCategoryToUser(Category category, User user) {
        category.setUser(user);
        categoryRepository.save(category);
    }

    public void renameCategory(long categoryId, String newName) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            category.setName(newName);
            categoryRepository.save(category);
        }
    }

    public void deleteCategory(long categoryId) {
        if (categoryRepository.existsById(categoryId))
            categoryRepository.findById(categoryId)
                    .ifPresent(categoryRepository::delete);
    }

    public Category getCategoryById(long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

}
