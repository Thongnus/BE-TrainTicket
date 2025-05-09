package com.example.betickettrain.service;

import com.example.betickettrain.entity.Role;

import java.util.Optional;

public interface RoleService {
 Optional<Role> findByName(String name);


}
