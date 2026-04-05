package org.brusnitsyn.financetracker.repository;

import java.util.List;
import java.util.Optional;
import org.brusnitsyn.financetracker.model.entity.Category;
import org.brusnitsyn.financetracker.model.entity.User;
import org.brusnitsyn.financetracker.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);

    List<Category> findByUserAndType(User user, TransactionType type);

    Optional<Category> findByIdAndUser(Long id, User user);

    boolean existsByUserAndNameIgnoreCaseAndType(User user, String name, TransactionType type);
}
