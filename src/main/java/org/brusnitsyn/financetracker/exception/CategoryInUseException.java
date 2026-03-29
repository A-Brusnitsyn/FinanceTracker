package org.brusnitsyn.financetracker.exception;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(Long id) {
        super("Category is used in transactions: id=" + id);
    }
}
