package org.brusnitsyn.financetracker.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long categoryId) {
        super("Category not found: id=" + categoryId);
    }
}
