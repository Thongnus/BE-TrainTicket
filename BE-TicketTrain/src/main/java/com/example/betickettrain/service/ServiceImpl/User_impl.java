package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.entity.Role;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.repository.RoleRepository;
import com.example.betickettrain.repository.UserRepository;
import com.example.betickettrain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class User_impl implements UserService {
    UserRepository userRepository;

    @Autowired
    RoleRepository role_Reponsitory;
@Autowired
    public User_impl(UserRepository userReponsitory) {
        this.userRepository = userReponsitory;

    }

    @Override
    public User findbyUsername(String name) {
        return userRepository.findByUsername(name);

    }
    //cần lưu ý
    @Override
    public User saveTT(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        System.out.println(encodedPassword);


         Role r=   role_Reponsitory.findByName("ROLE_USER");
         if(r!=null){
             user.getRoles().add(r);

         }else {Set<Role> rr = new HashSet<>();
        rr.add(new Role("ROLE_USER"));
        user.setRoles(rr);}
        return userRepository.save(user);

    }




    @Override
    public Page<User> getallUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

     @Override
    public User updatepassword(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        System.out.println(encodedPassword);
        return userRepository.save(user);
    }
    @Override
    public User updatenopassword(User user) {


        return userRepository.save(user);
    }

    @Override
    public User findbyId(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteuserbyID(int id) {
         userRepository.deleteById(id);
    }

    @Override
    public ArrayList<User> searchbyName(String name) {
        return userRepository.findByUsernameContaining(name);
    }


}
