package org.brusnitsyn.financetracker.repository;

import org.brusnitsyn.financetracker.model.entity.Account;
import org.brusnitsyn.financetracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);

    Optional<Account> findByIdAndUserId(Long id, Long UserId);

    Long user(User user);
}
