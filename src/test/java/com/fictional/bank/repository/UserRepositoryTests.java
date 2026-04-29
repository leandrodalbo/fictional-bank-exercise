package com.fictional.bank.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class UserRepositoryTests
{

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));


    @Autowired
    protected UserRepository userRepository;

    @Test
    void shouldCheckUserByMail()
    {
        assertThat(userRepository.existsByEmail("john.smith@test.com")).isTrue();
    }

    @Test
    void shouldFindUserByMail()
    {
        assertThat(userRepository.findByEmail("john.smith@test.com").getName()).isNotEmpty();
    }

    @Test
    void shouldCheckTheUserHasAccounts()
    {
        assertThat(userRepository.hasAccounts(1L)).isTrue();
    }
}
