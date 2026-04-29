package com.fictional.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fictional.bank.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    boolean existsByEmail(String email);
    User findByEmail(String email);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.user.id = :userId")
    boolean hasAccounts(Long userId);
}
