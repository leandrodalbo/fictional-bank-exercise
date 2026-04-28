package com.fictional.bank.repository;

import java.util.Set;

import com.fictional.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>
{
    Set<Account> findByUserId(Long userId);
}

