package com.fictional.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fictional.bank.entity.Account;
import com.fictional.bank.entity.User;
import com.fictional.bank.repository.AccountRepository;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateTransactionRequest;
import com.fictional.bank.request.LoginRequest;
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

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TransactionsTest
{

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Test
    void shouldDepositMoneyIntoOwnAccount() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getOwnAccount(user);
        String token = getToken(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(new BigDecimal("100.00"), "GBP", "deposit", "ref");

        mockMvc.perform(post("/v1/accounts/" + account.getAccountNumber() + "/transactions")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("deposit"))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void shouldWithdrawMoneyWhenSufficientFunds() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getOwnAccount(user);

        account.setBalance(new BigDecimal("200.00"));
        accountRepository.save(account);

        String token = getToken(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(new BigDecimal("50.00"), "GBP", "withdrawal", "ref");

        mockMvc.perform(post("/v1/accounts/" + account.getAccountNumber() + "/transactions")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("withdrawal"));
    }

    @Test
    void shouldFailWithdrawalWhenInsufficientFunds() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getOwnAccount(user);

        account.setBalance(new BigDecimal("10.00"));
        accountRepository.save(account);

        String token = getToken(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(new BigDecimal("50.00"), "GBP", "withdrawal", "ref");

        mockMvc.perform(post("/v1/accounts/" + account.getAccountNumber() + "/transactions")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenForAnotherUsersAccountTransaction() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getAnotherUsersAccount(user);
        String token = getToken(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(new BigDecimal("50.00"), "GBP", "deposit", "ref");

        mockMvc.perform(post("/v1/accounts/" + account.getAccountNumber() + "/transactions")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundForNonExistentAccountTransaction() throws Exception {
        User user = getTestingUsers().get(0);
        String token = getToken(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(new BigDecimal("50.00"), "GBP", "deposit", "ref");

        mockMvc.perform(post("/v1/accounts/99999999/transactions")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenMissingFields() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getOwnAccount(user);
        String token = getToken(user);

        CreateTransactionRequest request =
                new CreateTransactionRequest(null, "GBP", "deposit", "ref");

        mockMvc.perform(post("/v1/accounts/" + account.getAccountNumber() + "/transactions")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldListOwnTransactions() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getOwnAccount(user);
        String token = getToken(user);

        mockMvc.perform(get("/v1/accounts/" + account.getAccountNumber() + "/transactions")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").exists());
    }

    @Test
    void shouldReturnForbiddenWhenListingAnotherUsersTransactions() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getAnotherUsersAccount(user);
        String token = getToken(user);

        mockMvc.perform(get("/v1/accounts/" + account.getAccountNumber() + "/transactions")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenListingTransactionsForNonExistentAccount() throws Exception {
        User user = getTestingUsers().get(0);
        String token = getToken(user);

        mockMvc.perform(get("/v1/accounts/99999999/transactions")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturnNotFoundForNonExistentTransaction() throws Exception {
        User user = getTestingUsers().get(0);
        Account account = getOwnAccount(user);
        String token = getToken(user);

        mockMvc.perform(get("/v1/accounts/" + account.getAccountNumber() + "/transactions/tan-1232")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private String getToken(User user) throws Exception {
        LoginRequest request = new LoginRequest(user.getEmail());

        String json = mockMvc.perform(post("/v1/users/login")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("token").asText();
    }

    private List<User> getTestingUsers() {
        return userRepository.findAll();
    }

    private Account getOwnAccount(User user) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow();
    }

    private Account getAnotherUsersAccount(User user) {
        return accountRepository.findAll().stream()
                .filter(a -> !a.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow();
    }
}