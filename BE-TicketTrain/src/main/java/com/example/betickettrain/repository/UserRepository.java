package com.example.betickettrain.repository;

import com.example.betickettrain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> , JpaSpecificationExecutor<User> {
    User findByUsername(String name);
    boolean existsUserByUsername(String username);

    @Query("select u  from User u where u.userId=:id")
    User findById(@Param("id") Long id);
    @Query("SELECT  u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_USER'")
    Page<User> findAllUsersWithRoleUser(Pageable pageable);
    ArrayList<User> findByUsernameContaining(String username);

}