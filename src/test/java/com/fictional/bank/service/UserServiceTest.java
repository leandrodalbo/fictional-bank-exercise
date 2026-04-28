package com.fictional.bank.service;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.model.User;
import com.fictional.bank.model.UserAddress;
import com.fictional.bank.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import com.fictional.bank.exception.ApiException;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateUserRequest;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


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
}
