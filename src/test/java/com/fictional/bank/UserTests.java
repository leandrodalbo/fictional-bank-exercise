package com.fictional.bank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fictional.bank.model.UserAddress;
import com.fictional.bank.request.CreateUserRequest;

@SpringBootTest
@AutoConfigureMockMvc
class UserTests extends TestContainersSetup {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldCreateANewUser() throws Exception {
		CreateUserRequest request = new CreateUserRequest(
			"Test User",
			new UserAddress("line1", "line2", "line3", "town", "county", "postcode"),
			"+441234567890",
			"integration@test.com"
		);

		mockMvc.perform(post("/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.email").value("integration@test.com"));
	}
}