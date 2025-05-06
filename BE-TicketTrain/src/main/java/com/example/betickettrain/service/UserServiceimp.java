package com.example.betickettrain.service;

import com.example.betickettrain.entity.User;
import com.example.betickettrain.repository.RoleRepository;
import com.example.betickettrain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceimp implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @Cacheable(value = "userCache", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Fetching user details for username: {}", username);

        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        log.debug("User found with username: {}", username);
        return user;
    }

    // Thêm phương thức để tìm user theo ID
    @Transactional
    @Cacheable(value = "userCache", key = "#id")
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    // Thêm phương thức để lưu user mới hoặc cập nhật user
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Thêm phương thức kiểm tra username đã tồn tại chưa
    @Transactional
    public boolean existsByUsername(String username) {
        return userRepository.existsUserByUsername(username);
    }
}