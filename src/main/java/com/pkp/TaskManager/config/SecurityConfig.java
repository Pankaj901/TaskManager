package com.pkp.TaskManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("USER")
                .build();

        UserDetails superuser = User.withUsername("superadmin")
                .password(passwordEncoder().encode("admin456"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, superuser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Secure encoding
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // ✅ modern way to disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/tasks", "/tasks/describe/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tasks").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/tasks/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tasks/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults()); // ✅ basic auth enabled

        return http.build();
    }
}
