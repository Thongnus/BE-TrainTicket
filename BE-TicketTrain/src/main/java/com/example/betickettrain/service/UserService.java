package com.example.betickettrain.service;


import com.example.betickettrain.dto.SignupRequest;
import com.example.betickettrain.dto.UserDto;
import com.example.betickettrain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.ArrayList;

public interface UserService {
    User findbyUsername(String name);

    User saveTT(User user);

    // UserService method
    UserDto registerUser(SignupRequest signupRequest) throws RoleNotFoundException;

    Page<User> getallUser(Pageable pageable);

    User updatepassword(User u);

    User update(UserDto user, String username);

    User updatenopassword(User user);

    User findbyId(Long id);

    void deleteuserbyID(int id);

    ArrayList<User> searchbyName(String name);

    Page<UserDto> findUsers(String search, String role, String status, Pageable pageable);
    UserDto createUser(UserDto userDto);
//    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto toggleStatus(Long id, String status);
    UserDto findUserById(Long id);
}
