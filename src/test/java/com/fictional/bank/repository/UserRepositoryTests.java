package com.fictional.bank.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fictional.bank.TestContainersSetup;

@DataJpaTest
class UserRepositoryTests extends TestContainersSetup
{

	@Autowired
	private UserRepository userRepository;

	@Test
	void shouldFindUserByMail() {
		assertThat(userRepository.existsByEmail("john.smith@test.com")).isTrue();
	}

}
