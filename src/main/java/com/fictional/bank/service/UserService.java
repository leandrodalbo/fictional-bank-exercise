package com.fictional.bank.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiException;
import com.fictional.bank.exception.ApiLoginException;
import com.fictional.bank.model.User;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.response.LoginResponse;
import com.fictional.bank.response.UserResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserService
{
    private String secret;
    private Long tokenExpirationTime;
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository,
                      @Value("${jwt.secret}")
                       String secret,
                       @Value("${jwt.expiration}")
                       Long tokenExpirationTime)
    {
        this.secret = secret;
        this.tokenExpirationTime = tokenExpirationTime;
        this.userRepository = userRepository;
    }

    public UserResponse createNewUser(CreateUserRequest request)
    {
        if (userRepository.existsByEmail(request.getEmail()))
        {
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


    public LoginResponse login(LoginRequest request)
    {
        if (!userRepository.existsByEmail(request.email())) throw new ApiLoginException(ApiErrorMessage.USER_NOT_FOUND);

        String token = Jwts.builder()
                .setSubject(request.email())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        return new LoginResponse(token);
    }
}
