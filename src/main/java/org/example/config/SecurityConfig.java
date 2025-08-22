package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private static final String[] PUBLIC_ENDPOINTS = {
        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health", "/actuator/metrics", "/actuator/prometheus"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if ("prod".equalsIgnoreCase(System.getProperty("spring.profiles.active", System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", "dev")))) {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                    .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh.authenticationEntryPoint((request, response, authException) -> {
                    if (response.getStatus() == 401) {
                        response.sendError(404, "Not Found");
                    }
                }))
                .httpBasic(org.springframework.security.config.Customizer.withDefaults());
        } else {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
                );
        }
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("admin")
            .password("{noop}admin123")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}
