package org.brusnitsyn.financetracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.CategoryCreateRequest;
import org.brusnitsyn.financetracker.model.dto.CategoryResponse;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.model.mappers.CategoryMapper;
import org.brusnitsyn.financetracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getCategories(Long userId, TransactionType type){
        log.info("Fetching categories for userId ={} type={}",userId,type);

        return (type==null ? categoryRepository.findByUserId(userId) : categoryRepository.findByUserIdAndType(userId, type))
                .stream()
                .map(categoryMapper::categoryToResponse)
                .toList();
    }

    public CategoryResponse createCategory(CategoryCreateRequest request){
        log.info("Creating category name={}, type={}, userId={}", request.getName(), request.getType(), request.getUserId());

        boolean exists = categoryRepository.existsByUserIdAndNameIgnoreCaseAndType(request.getUserId(), request.getName(), request.getType());

        if (exists) {
            throw new IllegalStateException("Category already exists");
        }

        Category category=Category.builder()
                .name(request.getName())
                .type(request.getType())
                .user(User.builder().id(request.getUserId()).build())
                .build();

        Category saved = categoryRepository.save(category);

        log.info("Category name={} created for user userId={}", category.getName(), request.getUserId());

        return categoryMapper.categoryToResponse(saved);
    }
}
