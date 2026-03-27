package org.brusnitsyn.financetracker.controller;

import lombok.RequiredArgsConstructor;
import org.brusnitsyn.financetracker.model.dto.CategoryResponse;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
}
