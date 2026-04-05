package org.brusnitsyn.financetracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Endpoints for managing transaction categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Get user categories",
            description =
                    "Returns all categories for a user, optionally filtered by transaction type")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Categories retrieved successfully"),
                @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping
    public List<CategoryResponse> getCategories(
            @RequestParam(required = false) TransactionType type) {
        log.debug("GET /categories");
        return categoryService.getCategories(type);
    }

    @Operation(
            summary = "Create new category",
            description = "Creates a new transaction category for the user")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Category created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(
                        responseCode = "409",
                        description = "Category already exists for this user")
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        log.info("Create category request");
        return categoryService.createCategory(request);
    }

    @Operation(
            summary = "Delete category",
            description = "Deletes a category if it is not used in any transactions")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Category not found"),
                @ApiResponse(
                        responseCode = "409",
                        description = "Category is in use and cannot be deleted")
            })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        log.info("Delete category request");
        categoryService.deleteCategory(id);
    }
}
