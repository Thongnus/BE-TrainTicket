package com.example.betickettrain.service;

import com.example.betickettrain.dto.RoleDto;
import com.example.betickettrain.entity.Role;

import java.util.Optional;
import java.util.List;
public interface RoleService {
 Optional<Role> findByName(String name);


    List<RoleDto> getAllRoles();
}
