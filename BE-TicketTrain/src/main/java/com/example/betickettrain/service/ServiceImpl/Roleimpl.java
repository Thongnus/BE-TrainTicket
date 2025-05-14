package com.example.betickettrain.service.ServiceImpl;


import com.example.betickettrain.entity.Role;
import com.example.betickettrain.repository.RoleRepository;
import com.example.betickettrain.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Roleimpl implements RoleService {
    @Autowired
    RoleRepository roleReponsitory;

    @Override
    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(roleReponsitory.findByName(name));
    }
}
