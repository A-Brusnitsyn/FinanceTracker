package org.brusnitsyn.financetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.brusnitsyn.financetracker.model.dto.CategoryCreateRequest;
import org.brusnitsyn.financetracker.model.dto.CategoryResponse;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getCategories(@RequestParam Long userId, @RequestParam(required = false) TransactionType type) {
        return categoryService.getCategories(userId, type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryCreateRequest request){
        return categoryService.createCategory(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id, @RequestParam Long userId){
        categoryService.deleteCategory(userId, id);
    }
}
