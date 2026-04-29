package com.fictional.bank;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fictional.bank.model.AccountType;
import com.fictional.bank.model.User;
import com.fictional.bank.model.UserAddress;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateAccountRequest;
import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.request.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Testcontainers
class AccountTests
{
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));
    private final UpdateUserRequest updateUserRequest = new UpdateUserRequest(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("updated@mail.com"));

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Test
    void shouldCreateANewUserAccount() throws Exception
    {
        CreateAccountRequest request = new CreateAccountRequest("My Personal Account", AccountType.PERSONAL);
        List<User> users = getTestingUsers();
        User user = users.get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(post("/v1/accounts")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sortCode").exists())
                .andExpect(jsonPath("$.accountNumber").exists());
    }

    private String getLoginToken(LoginRequest request) throws Exception
    {
        String json = mockMvc.perform(post("/v1/users/login")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("token").asText();
    }

    private LoginRequest loginRequest(User user)
    {
        return new LoginRequest(user.getEmail());
    }

    private List<User> getTestingUsers()
    {
        return userRepository.findAll();
    }
}