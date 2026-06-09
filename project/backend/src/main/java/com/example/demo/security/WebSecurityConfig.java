package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.services.UserDetailsServiceImpl;
import com.example.demo.security.jwt.JwtAuthEntryPoint;
import com.example.demo.security.jwt.JwtAuthTokenFilter;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Autowired
    public JwtAuthTokenFilter jwtAuthTokenFilter;

    @Bean
    DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // (ADMIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/users")
                        .hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/users/*/role").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/users/*").hasRole("ADMIN")

                        // (TEACHER, ADMIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/grades")
                        .hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/grades")
                        .hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/grades/*")
                        .hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/grades/*")
                        .hasAnyRole("TEACHER", "ADMIN")

                        // write/modify/delete (TEACHER, ADMIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/subjects")
                        .hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/subjects")
                        .hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/subjects/*/users/*")
                        .hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/subjects/*")
                        .hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/subjects/*")
                        .hasAnyRole("TEACHER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/subjects/*/users/*")
                        .hasAnyRole("TEACHER", "ADMIN")

                        .anyRequest().authenticated())
                .exceptionHandling(unauthorized -> unauthorized
                        .authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
