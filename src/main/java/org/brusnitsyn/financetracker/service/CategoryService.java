package org.brusnitsyn.financetracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.exception.CategoryAlreadyExistsException;
import org.brusnitsyn.financetracker.exception.CategoryInUseException;
import org.brusnitsyn.financetracker.exception.CategoryNotFoundException;
import org.brusnitsyn.financetracker.model.dto.CategoryCreateRequest;
import org.brusnitsyn.financetracker.model.dto.CategoryResponse;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.model.mappers.CategoryMapper;
import org.brusnitsyn.financetracker.repository.CategoryRepository;
import org.brusnitsyn.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public List<CategoryResponse> getCategories(TransactionType type){
        User user =currentUserService.getCurrentUser();
        log.info("Fetching categories for user ={} type={}",user.getEmail(),type);
        List<CategoryResponse> categories = (type==null ? categoryRepository.findByUser(user) : categoryRepository.findByUserAndType(user, type))
                .stream()
                .map(categoryMapper::categoryToResponse)
                .toList();
        log.info("Found {} categories for user={}", categories.size(), user.getEmail());

        return categories;
    }

    public CategoryResponse createCategory(CategoryCreateRequest request){
        User user =currentUserService.getCurrentUser();
        log.info("Creating category name={}, type={}, user={}", request.getName(), request.getType(), user.getEmail());

        boolean exists = categoryRepository.existsByUserAndNameIgnoreCaseAndType(user, request.getName(), request.getType());

        if (exists) {
            throw new CategoryAlreadyExistsException(request.getName());
        }

        Category category=Category.builder()
                .name(request.getName())
                .type(request.getType())
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);

        log.info("Category name={} created for user={}", category.getName(), user.getEmail());

        return categoryMapper.categoryToResponse(saved);
    }

    public void deleteCategory(Long categoryId){
        User user =currentUserService.getCurrentUser();
        log.info("Deleting category id={} for user={}", categoryId, user.getEmail());

        Category category=categoryRepository.findByIdAndUser(categoryId, user).
                orElseThrow(()->new CategoryNotFoundException(categoryId));

        boolean used=transactionRepository.existsByCategoryId(categoryId);

        if (used){
            throw new CategoryInUseException(categoryId);
        }

        categoryRepository.delete(category);

        log.info("Category deleted id={}", categoryId);
    }
}