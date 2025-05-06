package com.example.betickettrain.dto;

import com.example.betickettrain.entity.Role;
import com.example.betickettrain.entity.User;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.example.betickettrain.entity.User}
 */
@Value
public class UserDto implements Serializable {
    Integer userId;
    String username;
    String password;
    String fullName;
    String email;
    String phone;
    String address;
    String idCard;
    LocalDate dateOfBirth;
    Set<Role> role;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}