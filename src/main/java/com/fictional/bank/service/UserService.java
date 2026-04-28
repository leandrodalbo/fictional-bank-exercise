package com.fictional.bank.service;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiException;
import com.fictional.bank.model.User;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    private UserRepository userRepository;

    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public UserResponse createNewUser(CreateUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ApiErrorMessage.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(request.getName())
                .address(request.getAddress())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User saved = this.userRepository.save(user);

        return new UserResponse(
                saved.getId().toString(),
                saved.getName(),
                saved.getAddress(),
                saved.getPhoneNumber(),
                saved.getEmail(),
                saved.getCreatedAt().toString(),
                saved.getUpdatedAt().toString()
        );
    }
}
