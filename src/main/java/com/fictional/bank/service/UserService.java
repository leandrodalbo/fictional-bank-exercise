package com.fictional.bank.service;

import java.util.Date;
import java.util.UUID;

import com.fictional.bank.exception.ApiNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;
    private final String secret;
    private final Long tokenExpirationTime;


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

    public UserResponse userDetails(long userId, String userMail)
    {

        User saved = userRepository.findById(userId).orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.USER_NOT_FOUND));

        if(!saved.getEmail().equals(userMail)) throw new AccessDeniedException(ApiErrorMessage.INVALID_REQUEST.getMessage());

        return new UserResponse(
                saved.getId().toString(),
                saved.getName(),
                saved.getAddress(),
                saved.getPhoneNumber(),
                saved.getEmail(),
                saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : "",
                saved.getUpdatedAt() != null ? saved.getUpdatedAt().toString() : ""
        );
    }


}
