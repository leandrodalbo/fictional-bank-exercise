package com.fictional.bank.service;

import java.util.Date;

import com.fictional.bank.exception.ApiNotDeletableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fictional.bank.exception.ApiErrorMessage;
import com.fictional.bank.exception.ApiException;
import com.fictional.bank.exception.ApiLoginException;
import com.fictional.bank.exception.ApiNotFoundException;
import com.fictional.bank.model.User;
import com.fictional.bank.repository.UserRepository;
import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.request.UpdateUserRequest;
import com.fictional.bank.response.LoginResponse;
import com.fictional.bank.response.UserResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static com.fictional.bank.utils.Utils.validateUser;

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

    @Transactional
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

        validateUser(saved.getEmail(), userMail);

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

    @Transactional
    public UserResponse updateUserDetails(long userId, String userMail, UpdateUserRequest request)
    {

        User saved = userRepository.findById(userId)
                .orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.USER_NOT_FOUND));

        validateUser(saved.getEmail(), userMail);

        request.getName().ifPresent(saved::setName);
        request.getAddress().ifPresent(saved::setAddress);
        request.getPhoneNumber().ifPresent(saved::setPhoneNumber);
        request.getEmail().ifPresent(saved::setEmail);

        User updated = userRepository.save(saved);

        return new UserResponse(
                updated.getId().toString(),
                updated.getName(),
                updated.getAddress(),
                updated.getPhoneNumber(),
                updated.getEmail(),
                updated.getCreatedAt() != null ? updated.getCreatedAt().toString() : "",
                updated.getUpdatedAt() != null ? updated.getUpdatedAt().toString() : ""
        );
    }


    @Transactional
    public void deleteUserDetails(long userId, String userMail)
    {

        User saved = userRepository.findById(userId)
                .orElseThrow(() -> new ApiNotFoundException(ApiErrorMessage.USER_NOT_FOUND));

        validateUser(saved.getEmail(), userMail);

        if (userRepository.hasAccounts(userId)) throw new ApiNotDeletableException(ApiErrorMessage.INVALID_REQUEST);

        userRepository.deleteById(userId);
    }


}
