package com.fictional.bank.repository;

import java.util.List;

import com.fictional.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>
{
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId")
    List<Transaction> findByAccountId(Long accountId);
}

