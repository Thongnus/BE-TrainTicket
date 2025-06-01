package com.example.betickettrain.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.betickettrain.entity.Role}
 */
@Value
public class RoleDto implements Serializable {
    int id;
    String name;
}