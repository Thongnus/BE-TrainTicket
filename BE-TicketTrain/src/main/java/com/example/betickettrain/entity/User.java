package com.example.betickettrain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User  implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

//    @Column(nullable = true, unique = true, length = 50)
    private String username;

//    @Column(nullable = true, length = 255)
    private String password;

//    @Column(nullable = true, length = 100)
    private String fullName;

//    @Column(nullable = true, unique = true, length = 100)
    private String email;

//    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

//    @Column(nullable = true, unique = true, length = 20)
    private String idCard;

    private LocalDate dateOfBirth;

    @ManyToMany(fetch = FetchType.EAGER,cascade =CascadeType.PERSIST)
    @JoinTable(
            name ="users_roles",
            joinColumns =@JoinColumn(name = "user_id"),
            inverseJoinColumns =@JoinColumn(name = "role_id")
    )
    private Set<Role> roles= new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        return authorities;
    }

}