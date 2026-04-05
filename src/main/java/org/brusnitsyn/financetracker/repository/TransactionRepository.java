package org.brusnitsyn.financetracker.repository;

import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.Transaction;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
                SELECT t FROM Transaction t
                WHERE t.user = :user
                AND t.date >= COALESCE(:from, t.date)
                AND t.date <= COALESCE(:to, t.date)
                AND (:type IS NULL OR t.type = :type)
                AND (:account IS NULL OR t.account = :account)
                AND (:category IS NULL OR t.category = :category)
                ORDER BY t.date DESC
            """)
    Page<Transaction> findUserTransactions(
            @Param("user") User user,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("type") TransactionType type,
            @Param("account") Account account,
            @Param("category") Category category,
            Pageable pageable
    );


    boolean existsByCategoryId(Long categoryId);
}