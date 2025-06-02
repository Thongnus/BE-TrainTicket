package com.example.betickettrain.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {
    private String userName;
    private String passWord;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Set<String> roles;
}