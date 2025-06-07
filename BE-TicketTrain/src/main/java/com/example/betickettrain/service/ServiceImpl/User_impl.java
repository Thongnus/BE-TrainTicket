package com.example.betickettrain.service.ServiceImpl;

import com.example.betickettrain.anotation.LogAction;
import com.example.betickettrain.dto.SignupRequest;
import com.example.betickettrain.dto.UserDto;
import com.example.betickettrain.entity.Role;
import com.example.betickettrain.entity.User;
import com.example.betickettrain.exceptions.ErrorCode;
import com.example.betickettrain.exceptions.UserNotFoundException;
import com.example.betickettrain.mapper.UserMapper;
import com.example.betickettrain.repository.RoleRepository;
import com.example.betickettrain.repository.UserRepository;
import com.example.betickettrain.service.UserService;
import com.example.betickettrain.util.Constants;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class User_impl implements UserService {
    private final PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleRepository role_Reponsitory;

    @Autowired
    public User_impl(UserRepository userReponsitory, PasswordEncoder passwordEncoder) {
        this.userRepository = userReponsitory;

        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findbyUsername(String name) {
        return userRepository.findByUsername(name);

    }

    @Transactional
    //cần lưu ý
    @Override
    public User saveTT(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        System.out.println(encodedPassword);


        Role r = role_Reponsitory.findByName("ROLE_USER");
        if (r != null) {
            user.getRoles().add(r);

        } else {
            Set<Role> rr = new HashSet<>();
            rr.add(new Role("ROLE_USER"));
            user.setRoles(rr);
        }
        return userRepository.save(user);

    }

    @Transactional
    @Override
    public UserDto registerUser(SignupRequest signupRequest) throws RoleNotFoundException {
        if (userRepository.existsUserByUsername(signupRequest.getUserName())) {
            throw new RuntimeException("Username is already taken!");
        }

        User user = createUserFromRequest(signupRequest);
        return userMapper.toDto(userRepository.save(user));


    }

    private User createUserFromRequest(SignupRequest request) throws RoleNotFoundException {
        User user = new User();
        user.setUsername(request.getUserName());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassWord()));
        user.setRoles(assignRoles(request.getRoles()));

        return user;
    }

    @Override
    public Page<User> getallUser(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User updatepassword(User user) {

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    @LogAction(action = Constants.Action.UPDATE, entity = "User", description = "Update a user")
    public User update(UserDto userDto, String username) {
        // Tìm user hiện tại bằng username
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new UserNotFoundException("USER_NOT_FOUND",ErrorCode.USER_NOT_FOUND.message);
        }
//        if(userDto.getUsername() != null && userRepository.existsUserByUsername(userDto.getUsername())) {
//            throw new UserNotFoundException("USER_ALREADY",ErrorCode.USERNAME_ALREADY.message);
//        }
    //    userDto.setUsername(userDto.getUsername());

        // Cập nhật thông tin (chỉ update field không null)
        if (userDto.getFullName() != null) {
            existingUser.setFullName(userDto.getFullName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getPhone() != null) {
            existingUser.setPhone(userDto.getPhone());
        }
        if (userDto.getAddress() != null) {
            existingUser.setAddress(userDto.getAddress());
        }
        if(userDto.getIdCard() != null) {
            existingUser.setIdCard(userDto.getIdCard());
        }
        if(userDto.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDto.getDateOfBirth());
        }
        // Chỉ update password nếu có
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(userDto.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User updatenopassword(User user) {


        return userRepository.save(user);
    }

    private Set<Role> assignRoles(Set<String> requestedRoles) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        // Default role
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            Role userRole = role_Reponsitory.findByName(Constants.Role.ROLE_USER);
            if (userRole == null) throw new RoleNotFoundException("Role CUSTOMER not found");
            roles.add(userRole);
        }
        // Admin role if requested
        if (requestedRoles != null && requestedRoles.contains("ADMIN")) {
            Role adminRole = role_Reponsitory.findByName(Constants.Role.ROLE_ADMIN);
            if (adminRole == null) throw new RoleNotFoundException("Role ADMIN not found");
            roles.add(adminRole);
        }

        return roles;
    }

    @Override
    public User findbyId(Long id) {
        return userRepository.findById(id);
    }

    @LogAction(action = Constants.Action.DELETE, entity = "User", description = " Update a user")
    @Override
    public void deleteuserbyID(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public ArrayList<User> searchbyName(String name) {
        return userRepository.findByUsernameContaining(name);
    }

    @Override
    public Page<UserDto> findUsers(String search, String role, String status, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), searchPattern),
                        cb.like(cb.lower(root.get("fullName")), searchPattern),
                        cb.like(cb.lower(root.get("email")), searchPattern)
                ));
            }

            // FIX: Sử dụng join và so sánh theo tên role
            if (role != null && !role.equals("all")) {
                predicates.add(cb.equal(
                        root.join("roles").get("name"),
                        "ROLE_" + role.toUpperCase()
                ));
            }

            if (status != null && !status.equals("all")) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRoles(userDto.getRoles());
        user.setLastLogin(null);
        user.setStatus("active");
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }


    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(Math.toIntExact(id))) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + id);
        }
        userRepository.deleteById(Math.toIntExact(id));
    }

    @Override
    public UserDto toggleStatus(Long id, String status) {
        User user = userRepository.findById(id);
        if (user == null) throw new RuntimeException("Không tìm thấy người dùng với ID: " + id);
        if (!status.equals(Constants.User.STATUS_ACTIVE) && !status.equals(Constants.User.STATUS_INACTIVE) && !status.equals(Constants.User.STATUS_BAN)) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) throw new RuntimeException("Không tìm thấy người dùng với ID: " + id);
        return userMapper.toDto(user);
    }
}
