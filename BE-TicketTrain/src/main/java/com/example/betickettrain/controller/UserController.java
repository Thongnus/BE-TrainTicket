package com.example.betickettrain.controller;

import com.example.betickettrain.dto.UserDto;
import com.example.betickettrain.service.ServiceImpl.UserServiceimp;
import com.example.betickettrain.service.UserService;
import com.example.betickettrain.util.Constants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
        @RequestParam(required = false) String searchTerm,
        @RequestParam(defaultValue = "all") String role,
        @RequestParam(defaultValue = "all") String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserDto> users = userService.findUsers(searchTerm, role, status, pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
//        UserDto updatedUser = userService.updateUser(id, userDto);
//        return ResponseEntity.ok(updatedUser);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<UserDto> toggleUserStatus(@PathVariable Long id, @RequestBody String statusUpdate) {
        UserDto updatedUser = userService.toggleStatus(id,statusUpdate );
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }
}