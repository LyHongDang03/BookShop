package lyhongdang.book.service;

import lombok.RequiredArgsConstructor;
import lyhongdang.book.common.PageResponse;
import lyhongdang.book.dto.request.CategoryRequest;
import lyhongdang.book.dto.request.response.CategoryResponse;
import lyhongdang.book.entity.Book;
import lyhongdang.book.entity.Category;
import lyhongdang.book.enums.ErrorCodes;
import lyhongdang.book.handler.BusinessException;
import lyhongdang.book.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorySerVice {

    private final CategoryRepository categoryRepository;
    @PreAuthorize("hasAnyRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .nameCategory(categoryRequest.getName())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.builder()
                .name(savedCategory.getNameCategory())
                .build();
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    public CategoryResponse updateCategory(int id, CategoryRequest categoryRequest) {
        var categoryId = categoryRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCodes.CATEGORY_NOT_FOUND));
        categoryId.setNameCategory(categoryRequest.getName());
        Category savedCategory = categoryRepository.save(categoryId);
        return CategoryResponse.builder()
                .name(savedCategory.getNameCategory())
                .build();
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteCategory(int id) {
        var categoryId = categoryRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCodes.CATEGORY_NOT_FOUND));
        for (Book book : categoryId.getBooks()) {
            book.getCategories().remove(categoryId);
        }
        categoryRepository.delete(categoryId);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    public CategoryResponse getCategory(int id) {
        var categoryId = categoryRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCodes.CATEGORY_NOT_FOUND));
        return CategoryResponse.builder()
                .name(categoryId.getNameCategory())
                .build();
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    public PageResponse<CategoryResponse> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").ascending());
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category: categoryPage.getContent()) {
            CategoryResponse categoryResponse = CategoryResponse.builder()
                    .name(category.getNameCategory())
                    .build();
            categoryResponses.add(categoryResponse);
        }
        return PageResponse.<CategoryResponse>builder()
                .content(categoryResponses)
                .number(categoryPage.getNumber())
                .size(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .first(categoryPage.isFirst())
                .last(categoryPage.isLast())
                .build();
    }
}
