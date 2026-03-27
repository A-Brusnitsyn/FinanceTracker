package org.brusnitsyn.financetracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brusnitsyn.financetracker.model.dto.CategoryResponse;
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
        log.info("");
    }
}
