package org.brusnitsyn.financetracker.repository;

import org.brusnitsyn.financetracker.model.entity.Transaction;
import org.brusnitsyn.financetracker.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDesc(User user);

    Page<Transaction> findByUser(User user, Pageable pageable);


    boolean existsByCategoryId(Long categoryId);

    List<Transaction> findByUser(User user);
}