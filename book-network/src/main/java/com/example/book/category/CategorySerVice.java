package com.example.book.category;

import com.example.book.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorySerVice {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .nameCategory(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.builder()
                .name(savedCategory.getNameCategory())
                .description(savedCategory.getDescription())
                .build();
    }
    public CategoryResponse updateCategory(int id, CategoryRequest categoryRequest) {
        var categoryId = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        categoryId.setNameCategory(categoryRequest.getName());
        categoryId.setDescription(categoryRequest.getDescription());
        Category savedCategory = categoryRepository.save(categoryId);
        return CategoryResponse.builder()
                .name(savedCategory.getNameCategory())
                .description(savedCategory.getDescription())
                .build();
    }
    public void deleteCategory(int id) {
        var categoryId = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        for (Book book : categoryId.getBooks()) {
            book.getCategories().remove(categoryId);
        }
        categoryRepository.delete(categoryId);
    }
    public CategoryResponse getCategory(int id) {
        var categoryId = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryResponse.builder()
                .name(categoryId.getNameCategory())
                .description(categoryId.getDescription())
                .build();
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
