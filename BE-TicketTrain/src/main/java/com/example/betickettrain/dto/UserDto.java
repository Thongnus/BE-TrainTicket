package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.example.betickettrain.entity.User}
 */
@Value
public class UserDto implements Serializable {
    Long userId;
    String username;
    String password;
    String fullName;
    String email;
    String phone;
    String address;
    String idCard;
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dateOfBirth;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    Set<Role> roles;
    Instant lastLogin;
    String status; // Thêm status
}