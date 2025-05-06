// 5. DTO classes for requests and responses
package com.example.betickettrain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private Long id;
    private String username;
    private Collection<? extends GrantedAuthority> roles;
}