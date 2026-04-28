package com.fictional.bank;

import java.util.List;

import com.fictional.bank.model.User;
import com.fictional.bank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fictional.bank.model.UserAddress;
import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.request.LoginRequest;

@SpringBootTest
@AutoConfigureMockMvc
class UserTests extends TestContainersSetup
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

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

        mockMvc.perform(get("/v1/users/" + user.getId())
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

        mockMvc.perform(get("/v1/users/" + users.get(1).getId())
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

        mockMvc.perform(get("/v1/users/99999")
                                .header("Authorization", "Bearer " + token))
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
                .andExpect(status().isOk())
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
        return repository.findAll();
    }
}