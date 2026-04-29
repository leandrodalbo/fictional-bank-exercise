package com.fictional.bank.controller;


import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.request.UpdateUserRequest;
import com.fictional.bank.response.LoginResponse;

import com.fictional.bank.security.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.response.UserResponse;
import com.fictional.bank.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController
{
    private final UserService userService;
    private final AuthUtils authUtils;

    public UserController(UserService userService, AuthUtils authUtils)
    {
        this.userService = userService;
        this.authUtils = authUtils;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signUpUser(@Valid @RequestBody CreateUserRequest request)
    {
        return this.userService.createNewUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request)
    {
        return userService.login(request);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable String userId)
    {
        return userService.userDetails(extractUserId(userId), authUtils.getCurrentUser());
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse getUser(@PathVariable String userId, @RequestBody UpdateUserRequest updateUserRequest)
    {
        return userService.updateUserDetails(extractUserId(userId), authUtils.getCurrentUser(), updateUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId)
    {
        userService.deleteUserDetails(extractUserId(userId), authUtils.getCurrentUser());
    }

    private Long extractUserId(String userId){
        return  Long.valueOf(userId.split("usr-")[1]);
    }

}
