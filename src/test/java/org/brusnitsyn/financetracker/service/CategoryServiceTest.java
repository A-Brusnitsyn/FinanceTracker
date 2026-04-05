package org.brusnitsyn.financetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.brusnitsyn.financetracker.exception.CategoryAlreadyExistsException;
import org.brusnitsyn.financetracker.exception.CategoryInUseException;
import org.brusnitsyn.financetracker.exception.CategoryNotFoundException;
import org.brusnitsyn.financetracker.model.dto.CategoryCreateRequest;
import org.brusnitsyn.financetracker.model.dto.CategoryResponse;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.Role;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.brusnitsyn.financetracker.model.mappers.CategoryMapper;
import org.brusnitsyn.financetracker.repository.CategoryRepository;
import org.brusnitsyn.financetracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;

    @Mock private CategoryMapper categoryMapper;

    @Mock private TransactionRepository transactionRepository;

    @Mock private CurrentUserService currentUserService;

    @InjectMocks private CategoryService categoryService;

    private User testUser;
    private Category testCategory;
    private CategoryResponse testCategoryResponse;
    private CategoryCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser =
                User.builder()
                        .id(1L)
                        .email("test@example.com")
                        .name("Test User")
                        .role(Role.USER)
                        .build();

        testCategory =
                Category.builder()
                        .id(1L)
                        .user(testUser)
                        .name("Groceries")
                        .type(TransactionType.EXPENSE)
                        .build();

        testCategoryResponse =
                CategoryResponse.builder()
                        .id(1L)
                        .name("Groceries")
                        .type(TransactionType.EXPENSE)
                        .build();

        createRequest = new CategoryCreateRequest();
        createRequest.setName("Entertainment");
        createRequest.setType(TransactionType.EXPENSE);
    }

    @Test
    void getCategories_Success_WithoutTypeFilter() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findByUser(testUser)).thenReturn(List.of(testCategory));
        when(categoryMapper.categoryToResponse(testCategory)).thenReturn(testCategoryResponse);

        // When
        List<CategoryResponse> result = categoryService.getCategories(null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Groceries");

        verify(categoryRepository).findByUser(testUser);
        verify(categoryRepository, never()).findByUserAndType(any(), any());
    }

    @Test
    void getCategories_Success_WithTypeFilter() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findByUserAndType(testUser, TransactionType.EXPENSE))
                .thenReturn(List.of(testCategory));
        when(categoryMapper.categoryToResponse(testCategory)).thenReturn(testCategoryResponse);

        // When
        List<CategoryResponse> result = categoryService.getCategories(TransactionType.EXPENSE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(TransactionType.EXPENSE);

        verify(categoryRepository).findByUserAndType(testUser, TransactionType.EXPENSE);
    }

    @Test
    void getCategories_ReturnsEmptyList_WhenNoCategories() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findByUser(testUser)).thenReturn(List.of());

        // When
        List<CategoryResponse> result = categoryService.getCategories(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void createCategory_Success() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.existsByUserAndNameIgnoreCaseAndType(
                        testUser, "Entertainment", TransactionType.EXPENSE))
                .thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.categoryToResponse(testCategory)).thenReturn(testCategoryResponse);

        // When
        CategoryResponse result = categoryService.createCategory(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Groceries");

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_ThrowsException_WhenCategoryAlreadyExists() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.existsByUserAndNameIgnoreCaseAndType(
                        testUser, "Entertainment", TransactionType.EXPENSE))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.createCategory(createRequest))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessageContaining("Category already exists: Entertainment");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_Success() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findByIdAndUser(1L, testUser))
                .thenReturn(Optional.of(testCategory));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(false);

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void deleteCategory_ThrowsException_WhenCategoryInUse() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findByIdAndUser(1L, testUser))
                .thenReturn(Optional.of(testCategory));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(CategoryInUseException.class)
                .hasMessageContaining("Category is used in transactions: id=1");

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_ThrowsException_WhenCategoryNotFound() {
        // Given
        when(currentUserService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found: id=999");

        verify(categoryRepository, never()).delete(any());
    }
}
