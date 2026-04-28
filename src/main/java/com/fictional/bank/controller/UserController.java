package com.fictional.bank.controller;


import com.fictional.bank.request.LoginRequest;
import com.fictional.bank.request.UpdateUserRequest;
import com.fictional.bank.response.LoginResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public UserController(UserService userService)
    {
        this.userService = userService;
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
    public UserResponse getUser(@PathVariable Long userId)
    {
        return userService.userDetails(userId, getCurrentUser());
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse getUser(@PathVariable Long userId, @RequestBody UpdateUserRequest updateUserRequest)
    {
        return userService.updateUserDetails(userId, getCurrentUser(), updateUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId)
    {
        userService.deleteUserDetails(userId, getCurrentUser());
    }

    public String getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();

    }
}
