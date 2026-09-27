package com.hazmat.tracker.repository;

import com.hazmat.tracker.entity.Account;
import com.hazmat.tracker.entity.Enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCode(String code);
    List<Account> findByType(AccountType type);
}
