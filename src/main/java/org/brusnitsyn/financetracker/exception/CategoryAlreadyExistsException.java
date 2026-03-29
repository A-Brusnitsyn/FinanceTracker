package org.brusnitsyn.financetracker.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String name) {
        super("Category already exists: " + name);
    }
}
