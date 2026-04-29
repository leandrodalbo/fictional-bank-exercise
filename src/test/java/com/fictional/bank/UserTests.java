package com.fictional.bank;

import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fictional.bank.entity.User;
import com.fictional.bank.entity.UserAddress;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.request.UpdateUserRequest;

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
class UserTests
{
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:alpine"));
    private final UpdateUserRequest updateUserRequest = new UpdateUserRequest("updating-name", new UserAddress("l1", "l2", "l3", "town", "", ""), "+4498216847", "updated@mail.com");

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    /**
     * Scenario: User wants to delete their user details (no bank account)
     * Given a user has successfully authenticated
     * When the user makes a DELETE request to the /v1/users/{userId} endpoint
     * And they do not have a bank account
     * Then the system deletes their user
     */
    @Test
    void shouldDeleteOwnUserWithoutAccount() throws Exception
    {
        User user = getTestingUsers().stream().filter(it -> it.getEmail().contains("deleteme")).toList().get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(delete("/v1/users/" + prefixPlusUserId(user))
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    /**
     * Scenario: User wants to fetch their user details
     * Given a user has successfully authenticated
     * When the user makes a GET request to the /v1/users/{userId} endpoint supplying their userId
     * Then the system fetches the user details
     */
    @Test
    void shouldFetchOwnUserDetails() throws Exception
    {
        User user = getTestingUsers().get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(get("/v1/users/" + prefixPlusUserId(user))
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    /**
     * Scenario: User wants to fetch the user details of another user
     * Given a user has successfully authenticated
     * When the user makes a GET request to the /v1/users/{userId} endpoint supplying another user's userId
     * Then the system returns a Forbidden status code and error message
     */
    @Test
    void shouldReturnForbiddenWhenFetchingAnotherUser() throws Exception
    {
        List<User> users = getTestingUsers();
        LoginRequest loginRequest = loginRequest(users.get(0));
        String token = getLoginToken(loginRequest);

        mockMvc.perform(get("/v1/users/" + prefixPlusUserId(users.get(1)))
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Scenario: User wants to fetch the user details of a non-existent user
     * Given a user has successfully authenticated
     * When the user makes a GET request to the /v1/users/{userId} endpoint supplying a userId which doesn't exist
     * Then the system returns a Not Found status code and error message
     */
    @Test
    void shouldReturnNotFoundForNonExistentUser() throws Exception
    {
        User user = getTestingUsers().get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(get("/v1/users/usr-99999")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }


    /**
     * Scenario: User wants to update their user details
     * Given a user has successfully authenticated
     * When the user makes a PATCH request to the /v1/users/{userId} endpoint supplying their userId and all the required data
     * Then the system updates the user details and returns the updated data
     */
    @Test
    void shouldUpdateUserDetails() throws Exception
    {
        User user = getTestingUsers().get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(patch("/v1/users/" + prefixPlusUserId(user))
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(updateUserRequest.email()));
    }

    /**
     * Scenario: User wants to update the user details of another user
     * Given a user has successfully authenticated
     * When the user makes a PATCH request to the /v1/users/{userId} endpoint supplying another user's userId
     * Then the system returns a Forbidden status code and error message
     */
    @Test
    void shouldReturnForbiddenWhenUpdatingAnotherUser() throws Exception
    {
        List<User> users = getTestingUsers();
        LoginRequest loginRequest = loginRequest(users.get(0));
        String token = getLoginToken(loginRequest);

        mockMvc.perform(patch("/v1/users/" + prefixPlusUserId(users.get(1)))
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Scenario: User wants to update the user details of a non-existent user
     * Given a user has successfully authenticated
     * When the user makes a PATCH request to the /v1/users/{userId} endpoint supplying a userId which doesn't exist
     * Then the system returns a Not Found status code and error message
     */
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception
    {
        User user = getTestingUsers().get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(patch("/v1/users/usr-99999")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Scenario: Create a new user
     * Given a user wants to signup for Eagle Bank
     * When the user makes a `POST` request to the `/v1/users` endpoint with all the required data
     * Then a new user is created
     */
    @Test
    void shouldCreateANewUser() throws Exception
    {
        CreateUserRequest request = new CreateUserRequest(
                "Test User",
                new UserAddress("line1", "line2", "line3", "town", "county", "postcode"),
                "+441234567890",
                "integration@test.com"
        );

        mockMvc.perform(post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("integration@test.com"));
    }

    /**
     * Given a user has successfully authenticated
     * When the user makes a `POST` request to the `/v1/users` endpoint with required data missing
     * Then the system returns a Bad Request status code and error message
     */
    @Test
    void shouldGetBadRequestWithoutRequiredData() throws Exception
    {
        User user = getTestingUsers().get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        CreateUserRequest request = new CreateUserRequest(
                null,
                new UserAddress("line1", "line2", "line3", "town", "county", "postcode"),
                "+441234567890",
                "integration@test.com"
        );

        mockMvc.perform(post("/v1/users")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    /**
     * Scenario: User wants to delete their user details and they have a bank account
     * Given a user has successfully authenticated
     * When the user makes a DELETE request to the /v1/users/{userId} endpoint
     * And they have a bank account
     * Then the system returns a Conflict status code and error message
     */
    @Test
    void shouldReturnConflictWhenDeletingUserWithAccount() throws Exception
    {
        List<User> users = getTestingUsers();
        User user = users.get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(delete("/v1/users/" + prefixPlusUserId(user))
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Scenario: User wants to delete user details of another user
     * Given a user has successfully authenticated
     * When the user makes a DELETE request to the /v1/users/{userId} endpoint
     * And the userId is associated with another user
     * Then the system returns a Forbidden status code and error message
     */
    @Test
    void shouldReturnForbiddenWhenDeletingAnotherUser() throws Exception
    {
        List<User> users = getTestingUsers();
        LoginRequest loginRequest = loginRequest(users.get(0));
        String token = getLoginToken(loginRequest);

        mockMvc.perform(delete("/v1/users/" + prefixPlusUserId(users.get(1)))
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Scenario: User wants to delete user details of a non-existent user
     * Given a user has successfully authenticated
     * When the user makes a DELETE request to the /v1/users/{userId} endpoint
     * And the userId doesn't exist
     * Then the system returns a Not Found status code and error message
     */
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception
    {
        User user = getTestingUsers().get(0);
        LoginRequest loginRequest = loginRequest(user);
        String token = getLoginToken(loginRequest);

        mockMvc.perform(delete("/v1/users/usr-99999")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
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

    private String prefixPlusUserId(User user)
    {
        return "usr-" + user.getId();
    }


}