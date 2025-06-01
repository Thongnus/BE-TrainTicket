package com.example.betickettrain.controller;

import com.example.betickettrain.dto.Response;
import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoleController {
    private final RoleService roleService;

//    @PostMapping
//    public Response<?> createRole(@RequestBody RouteDto dto) {
//        return new Response<>(roleService.createRole(dto));
//    }

    @GetMapping
    public Response<?> getAllRoles() {
        return new Response<>(roleService.getAllRoles());
    }

//    @GetMapping("/{id}")
//    public Response<?> getRole(@PathVariable Integer id) {
//        return new Response<>(roleService.getRole(id));
//    }
//
//    @PutMapping("/{id}")
//    public Response<?> updateRole(@PathVariable Integer id, @RequestBody RouteDto dto) {
//        return new Response<>(roleService.updateRole(id, dto));
//    }
//
//    @DeleteMapping("/{id}")
//    public Response<?> deleteRole(@PathVariable Integer id) {
//        roleService.deleteRole(id);
//        return new Response<>("Role deleted successfully");
//    }
}
