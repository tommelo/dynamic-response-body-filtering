package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final List<UserResponse> users = createUsers();

    @GetMapping
    @ResponseBodyFilter
    public List<UserResponse> getAllUsers() {
        return users;
    }

    private List<UserResponse> createUsers() {
        UserResponse john = new UserResponse();
        john.setId(UUID.randomUUID().toString());
        john.setFirstName("John");
        john.setLastName("Doe");

        UserResponse jane = new UserResponse();
        jane.setId(UUID.randomUUID().toString());
        jane.setFirstName("Jane");
        jane.setLastName("Doakes");

        return Arrays.asList(john, jane);
    }

}