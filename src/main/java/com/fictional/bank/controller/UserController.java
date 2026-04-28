package com.fictional.bank.controller;

import com.fictional.bank.request.CreateUserRequest;
import com.fictional.bank.response.UserResponse;
import com.fictional.bank.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController
{
    private UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }


    @PostMapping
    public UserResponse signUpUser(@RequestBody CreateUserRequest request){
        return this.userService.createNewUser(request);
    }
}
