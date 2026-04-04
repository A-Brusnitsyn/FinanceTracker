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
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDesc(User user);

    @Query("""
    select t from Transaction t
    where t.user = :user
      and (:from is null or t.date >= :from)
      and (:to is null or t.date <= :to)
      and (:type is null or t.type = :type)
      and (:account is null or t.account = :account)
      and (:category is null or t.category = :category)
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

    List<Transaction> findByUser(User user);
}