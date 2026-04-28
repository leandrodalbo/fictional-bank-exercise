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
import com.fictional.bank.request.LoginRequest;

@SpringBootTest
@AutoConfigureMockMvc
class UserTests extends TestContainersSetup
{


    private final LoginRequest loginRequest =
            new LoginRequest("john.smith@test.com");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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

        CreateUserRequest request = new CreateUserRequest(
                null,
                new UserAddress("line1", "line2", "line3", "town", "county", "postcode"),
                "+441234567890",
                "integration@test.com"
        );

        mockMvc.perform(post("/v1/users")
                                .header("Authorization", "Bearer " + getLoginToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    private String getLoginToken() throws Exception
    {
        String json = mockMvc.perform(post("/v1/users/login")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("token").asText();
    }
}