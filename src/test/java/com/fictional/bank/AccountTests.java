package com.fictional.bank;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fictional.bank.model.Account;
import com.fictional.bank.model.User;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateBankAccountRequest;
import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.utils.Utils;
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


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AccountRepository accountRepository;

    /**
     * Given a user has successfully authenticated
     * When the user makes a `POST` request to the `/v1/accounts` endpoint with all the required data
     * Then a new bank account is created, and the account details are returned
     */
    @Test
    void shouldCreateANewUserAccount() throws Exception
    {
        CreateBankAccountRequest request = new CreateBankAccountRequest("My Personal Account", Utils.PERSONAL_ACCOUNT_TYPE);
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

    /**
     * Given a user has successfully authenticated
     * When the user makes a `POST` request to the `/v1/accounts` endpoint with required data missing
     * Then the system returns a Bad Request status code and error message
     */
    @Test
    void shouldNotCreateAUserAccount() throws Exception
    {
        CreateBankAccountRequest request = new CreateBankAccountRequest("", Utils.PERSONAL_ACCOUNT_TYPE);
        List<User> users = getTestingUsers();
        User user = users.get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(post("/v1/accounts")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    /**
     * Scenario: User wants to fetch their bank account details
     * Given a user has successfully authenticated
     * When the user makes a GET request to the /v1/accounts/{accountId} endpoint
     * And the account is associated with their userId
     * Then the system fetches the bank account details
     */
    @Test
    void shouldFetchOwnBankAccountDetails() throws Exception
    {
        List<User> users = getTestingUsers();
        User user = users.get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(get("/v1/accounts")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * Scenario: User wants to fetch another user's bank account details
     * Given a user has successfully authenticated
     * When the user makes a GET request to the /v1/accounts/{accountId} endpoint
     * And the account is not associated with their userId
     * Then the system returns a Forbidden status code and error message
     */
    @Test
    void shouldReturnForbiddenWhenFetchingAnotherUsersAccount() throws Exception
    {
        List<User> users = getTestingUsers();
        User user = users.get(0);
        Account account = getTestingAccounts().stream().filter(it -> !it.getUser().getId().equals(user.getId())).findFirst().get();

        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(get("/v1/accounts/" + account.getAccountNumber())
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    /**
     * Scenario: User wants to fetch a non-existent bank account
     * Given a user has successfully authenticated
     * When the user makes a GET request to the /v1/accounts/{accountId} endpoint
     * And the accountId doesn't exist
     * Then the system returns a Not Found status code and error message
     */
    @Test
    void shouldReturnNotFoundWhenFetchingNonExistentAccount() throws Exception
    {
        List<User> users = getTestingUsers();
        User user = users.get(0);
        String token = getLoginToken(loginRequest(user));

        mockMvc.perform(get("/v1/accounts/9999999")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

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

    private List<Account> getTestingAccounts()
    {
        return accountRepository.findAll();
    }
}