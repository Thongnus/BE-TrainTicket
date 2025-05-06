package com.example.betickettrain.service;


import com.example.betickettrain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

public interface UserService {
  User findbyUsername(String name);
  User saveTT(User user);

  Page<User> getallUser(Pageable pageable);
  User updatepassword(User u);

User update(User user);

    User updatenopassword(User user);

    User findbyId(Long id);
  void deleteuserbyID(int id);
  ArrayList<User> searchbyName(String name);
}
