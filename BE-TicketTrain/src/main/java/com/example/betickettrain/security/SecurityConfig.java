package com.example.betickettrain.security;

import com.example.betickettrain.configuration.AuthEntryPointJwt;
import com.example.betickettrain.configuration.JwtAuthenticationFilter;
import com.example.betickettrain.service.UserService;
import com.example.betickettrain.service.UserServiceimp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@
        Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    //    private final AuthenticationEntryPointImpl unauthorizedHandler;
    private final AuthEntryPointJwt authEntryPointJwt;

    public SecurityConfig(
            UserService userDetailsService,
            JwtAuthenticationFilter jwtAuthFilter,
            AuthEntryPointJwt authEntryPointJwt, AuthEntryPointJwt authEntryPointJwt1
//            AuthenticationEntryPointImpl unauthorizedHandler
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.authEntryPointJwt = authEntryPointJwt1;

//        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Nếu dùng JWT thì nên disable CSRF
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/all").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**", "/swagger-ui/index.html",
                                "/v3/api-docs.yaml", "/configuration/**").permitAll()
                        .requestMatchers("/api/test/user").hasRole("USER")
                        .requestMatchers("/api/test/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
              http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                      .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserServiceimp userServiceimp) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userServiceimp);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}