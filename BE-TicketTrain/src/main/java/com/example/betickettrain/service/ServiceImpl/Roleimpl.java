package com.example.betickettrain.service.ServiceImpl;


import com.example.betickettrain.dto.RoleDto;
import com.example.betickettrain.entity.Role;
import com.example.betickettrain.mapper.RoleMapper;
import com.example.betickettrain.repository.RoleRepository;
import com.example.betickettrain.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Roleimpl implements RoleService {
    @Autowired
    RoleRepository roleReponsitory;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(roleReponsitory.findByName(name));
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleReponsitory.findAll().stream().map(roleMapper::toDto).collect(Collectors.toList());
    }
}
