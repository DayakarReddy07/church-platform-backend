package com.church.church_platform.config;

import com.church.church_platform.security.JwtAuthFilter;
import com.church.church_platform.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // 🔓 Define which URLs are public and which need login
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Disable CSRF (not needed for REST APIs)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())

                // Define URL permissions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/churches/public").permitAll()
                        .requestMatchers("/api/churches/public/**").permitAll()
                        .requestMatchers("/api/churches/search").permitAll()

                        .requestMatchers("/api/sermons/public").permitAll()
                        .requestMatchers("/api/sermons/public/**").permitAll()
                        .requestMatchers("/api/sermons/search").permitAll()
                        .anyRequest().authenticated()
                )

                // No sessions — we use JWT tokens instead
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Add our JWT filter before Spring's default filter
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // 🔐 Password encoder — BCrypt hashes passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔑 Authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService); // 👈 one argument
        provider.setPasswordEncoder(passwordEncoder());            // 👈 setter for encoder
        return provider;
    }

    // 🎯 Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}