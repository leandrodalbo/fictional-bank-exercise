package com.fictional.bank.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiNotFoundException;
import com.fictional.bank.model.User;
import com.fictional.bank.model.UserAddress;
import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.response.LoginResponse;
import com.fictional.bank.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import com.fictional.bank.exception.ApiException;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateUserRequest;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{

    private final String secret = "uQw8vQ1pQ2t6bXJ5dGZzZ2hqa2xtbm9wcXJzdHV2d3h5eg==";
    private final Long expiration = 86400000L;
    private final User testingUser = new User(
            1001L,
            "user-name",
            new UserAddress("l1", "l2", "l3", "town", "", ""),
            "testinguser@mail.com",
            "",
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp()
    {
        userService = new UserService(userRepository, secret, expiration);
    }

    @Test
    void shouldNotCreateAUserIfTheEmailAlreadyExists()
    {
        String email = "any@mail.com";
        CreateUserRequest request = org.mockito.Mockito.mock(CreateUserRequest.class);
        when(request.getEmail()).thenReturn(email);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> userService.createNewUser(request))
                .withMessageContaining(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage());

        verify(userRepository).existsByEmail(email);
    }

    @Test
    void shouldCreateANewUser()
    {
        String email = "any@mail.com";
        String name = "Test User";
        String phone = "+441234567890";
        UserAddress address = new UserAddress("line1", "line2", "line3", "town", "county", "postcode");
        CreateUserRequest request = new CreateUserRequest(name, address, phone, email);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            user.setAddress(address);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            return user;
        });

        UserResponse res = userService.createNewUser(request);

        assertThat(res.getId()).isNotNull();
        assertThat(res.getAddress()).isNotNull();
        verify(userRepository).existsByEmail(email);
    }


    @Test
    void shouldLoginUser()
    {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        LoginResponse response = userService.login(new LoginRequest("usermail@mail.com"));

        assertThat(response.token()).isNotEmpty();
        verify(userRepository).existsByEmail(anyString());
    }

    @Test
    void shouldGetUserDetails()
    {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testingUser));

        UserResponse result = userService.userDetails(testingUser.getId(), testingUser.getEmail());

        assertThat(result.getName()).isEqualTo(testingUser.getName());

        verify(userRepository).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWhenNotFound()
    {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ApiNotFoundException.class)
                .isThrownBy(() -> userService.userDetails(1111L, testingUser.getEmail()))
                .withMessageContaining(ApiErrorMessage.USER_NOT_FOUND.getMessage());

        verify(userRepository).findById(anyLong());
    }


    @Test
    void shouldNotAccessTheDetailsOfAnotherUser()
    {
        String email = "userMail@mail.com";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testingUser));

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> userService.userDetails(testingUser.getId(), email))
                .withMessageContaining(ApiErrorMessage.INVALID_REQUEST.getMessage());

        verify(userRepository).findById(anyLong());
    }
}
