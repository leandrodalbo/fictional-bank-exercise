package com.fictional.bank.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class TransactionRepositoryTests
{

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));


    @Autowired
    protected TransactionRepository transactionRepository;

    @Test
    void shouldFindTransactionsByAccountID()
    {
        assertThat(transactionRepository.findByAccountId(1L).isEmpty()).isFalse();
    }

    @Test
    void shouldFindASingleTransaction()
    {
        assertThat(transactionRepository.findTransaction(1L, 1L).isPresent()).isTrue();
    }

}
